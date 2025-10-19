package jp.co.nintendo.chat.data.repository.impl.message.assistant

import androidx.paging.PagingData
import androidx.paging.map
import jp.co.nintendo.chat.data.repository.impl.message.assistant.factory.AiAssistantChatRequestFactory
import jp.co.nintendo.chat.data.repository.impl.message.factory.ChatMessageEntityFactory
import jp.co.nintendo.chat.data.repository.impl.message.mapper.ChatMessageMapper
import jp.co.nintendo.chat.data.repository.message.ChatMessageRepository
import jp.co.nintendo.chat.data.source.local.message.ChatMessageLocalDataSource
import jp.co.nintendo.chat.data.source.local.message.entity.ChatMessageEntity
import jp.co.nintendo.chat.data.source.local.message.model.ChatMessageInsertResult
import jp.co.nintendo.chat.data.source.remote.assistant.AiAssistantChatRemoteDataSource
import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageResponse
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ChoiceDto
import jp.co.nintendo.chat.model.message.ChatMessage
import jp.co.nintendo.chat.model.message.ChatMessageRequest
import jp.co.nintendo.chat.model.message.content.MessageContent
import jp.co.nintendo.chat.model.message.extras.AiAssistantExtras
import jp.co.nintendo.chat.model.message.lifecycle.MessageExchangeLifecycle
import jp.co.nintendo.chat.model.message.paging.MessagePageAnchor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * An implementation of [jp.co.nintendo.chat.data.repository.message.ChatMessageRepository] for AI Assistant chat
 */
class AiAssistantChatRepositoryImpl @Inject constructor(
    private val messageLocalDataSource: ChatMessageLocalDataSource,
    private val aiAssistantChatStreamDataSource: AiAssistantChatRemoteDataSource,
    private val aiAssistantChatRequestFactory: AiAssistantChatRequestFactory,
    private val chatMessageMapper: ChatMessageMapper,
    private val chatMessageEntityFactory: ChatMessageEntityFactory
) : ChatMessageRepository {
    override fun observeLatestMessage(channelId: String): Flow<ChatMessage?> =
        messageLocalDataSource.observeLatestMessage(channelId).map {
            it?.let { chatMessageMapper.mapToDomain(it) }
        }

    override suspend fun selectLatestMessage(channelId: String): ChatMessage? =
        messageLocalDataSource.selectLatestMessage(channelId)?.let(chatMessageMapper::mapToDomain)

    override suspend fun loadMessagePage(anchor: MessagePageAnchor): Flow<PagingData<ChatMessage>> =
        withContext(Dispatchers.IO) {
            loadMessagePage(channelId = anchor.channelId, initialKey = getInitialKey(anchor))
        }

    private fun loadMessagePage(channelId: String, initialKey: Int): Flow<PagingData<ChatMessage>> =
        messageLocalDataSource.selectMessagePagingSource(channelId, initialKey)
            .map { it.map(chatMessageMapper::mapToDomain) }


    override suspend fun selectMessage(localMessageId: String): ChatMessage? =
        withContext(Dispatchers.IO) {
            messageLocalDataSource.selectMessage(localMessageId)
                ?.let(chatMessageMapper::mapToDomain)
        }

    override suspend fun updateMessageContent(
        channelId: String,
        localMessageId: String,
        messageContent: MessageContent
    ): Boolean = withContext(Dispatchers.IO) {
        val currentMessage = messageLocalDataSource.selectMessage(localMessageId)
            ?.let(chatMessageMapper::mapToDomain) ?: return@withContext false
        val result = messageLocalDataSource.insert(
            chatMessageEntityFactory.create(
                channelId = channelId,
                message = currentMessage.copy(
                    content = messageContent
                )
            )
        )
        when (result) {
            ChatMessageInsertResult.Failure.FullDisk,
            ChatMessageInsertResult.Failure.Unknown -> false

            ChatMessageInsertResult.Success -> true
        }
    }

    private suspend fun getInitialKey(anchor: MessagePageAnchor): Int = when (anchor) {
        is MessagePageAnchor.Around -> {
            val entity = messageLocalDataSource.selectMessage(anchor.localMessageId)
            if (entity == null) {
                0
            } else {
                messageLocalDataSource.countNewerOrEqual(
                    channelId = anchor.channelId,
                    anchorLocalMessageId = entity.localMessageId,
                    anchorCreatedAt = entity.createdAtMillis
                )
            }
        }

        is MessagePageAnchor.Latest -> 0
    }

    override suspend fun exchangeMessage(
        channelId: String,
        messageRequest: ChatMessageRequest
    ): Flow<MessageExchangeLifecycle> = withContext(Dispatchers.IO) {
        flow {
            emit(MessageExchangeLifecycle.Sending)
            val sentMessageEntity = chatMessageEntityFactory.create(
                channelId = channelId,
                content = messageRequest.messageContent,
                senderExtras = messageRequest.senderExtras
            )

            val nullableFailure = commitMessage(sentMessageEntity)
            if (nullableFailure != null) {
                emit(nullableFailure)
                return@flow
            }
            exchangeLatestMessages(channelId, flowCollector = this)
        }
    }

    override suspend fun exchangeCurrentMessages(channelId: String): Flow<MessageExchangeLifecycle> =
        flow {
            emit(MessageExchangeLifecycle.Sending)
            exchangeLatestMessages(channelId, flowCollector = this)
        }

    override suspend fun deleteMessage(localMessageId: String) {
        messageLocalDataSource.deleteMessage(localMessageId)
    }

    private suspend fun exchangeLatestMessages(
        channelId: String,
        flowCollector: FlowCollector<MessageExchangeLifecycle>
    ) {
        val currentMessages = messageLocalDataSource.selectLatestMessages(channelId, 50)
            .map(chatMessageMapper::mapToDomain)

        flowCollector.emitAll(
            aiAssistantChatStreamDataSource.exchangeMessage(
                aiAssistantChatRequestFactory.create(currentMessages)
            ).transform {
                val nextEvent = handleResponse(channelId, it)
                if (nextEvent != null) {
                    emit(nextEvent)
                }
            }
        )
    }

    private fun handleResponse(
        channelId: String,
        response: AiAssistantExchangeMessageResponse
    ): MessageExchangeLifecycle? = when (response) {
        is AiAssistantExchangeMessageResponse.Done -> commitResponseMessage(channelId, response)

        is AiAssistantExchangeMessageResponse.Failure.Response,
        AiAssistantExchangeMessageResponse.Failure.Unknown -> MessageExchangeLifecycle.Failure

        is AiAssistantExchangeMessageResponse.InProgress -> mapToSendMessageProgressEvent(
            inProgressResponse = response
        )
    }

    private fun commitResponseMessage(
        channelId: String,
        response: AiAssistantExchangeMessageResponse.Done
    ): MessageExchangeLifecycle {
        val messageEntity = createResponseMessageEntity(
            channelId = channelId,
            responseId = response.responseId,
            choices = response.choices
        ) ?: return MessageExchangeLifecycle.Failure
        val nullableFailure = commitMessage(messageEntity)
        if (nullableFailure != null) {
            return MessageExchangeLifecycle.Failure
        }
        return MessageExchangeLifecycle.Done
    }

    private fun createResponseMessageEntity(
        channelId: String,
        responseId: String,
        choices: List<ChoiceDto>
    ): ChatMessageEntity? = choices.firstOrNull()?.let {
        chatMessageEntityFactory.createAiAssistantResponseMessage(
            channelId = channelId,
            responseId = responseId,
            choice = it
        )
    }

    private fun commitMessage(entity: ChatMessageEntity): MessageExchangeLifecycle.Failure? =
        when (messageLocalDataSource.insert(entity)) {
            ChatMessageInsertResult.Failure.FullDisk,
            ChatMessageInsertResult.Failure.Unknown -> MessageExchangeLifecycle.Failure

            ChatMessageInsertResult.Success -> null
        }

    private fun mapToSendMessageProgressEvent(
        inProgressResponse: AiAssistantExchangeMessageResponse.InProgress
    ): MessageExchangeLifecycle? {
        val firstChoice = inProgressResponse.choices.firstOrNull() ?: return null
        return when (firstChoice) {
            is AiAssistantExchangeMessageResponse.InProgress.ChoiceAssembleSnapshot.Content ->
                MessageExchangeLifecycle.StreamingResponseContent(
                    content = firstChoice.assembledContent,
                    senderExtras = AiAssistantExtras(responseId = inProgressResponse.responseId)
                )

            AiAssistantExchangeMessageResponse.InProgress.ChoiceAssembleSnapshot.ToolCall ->
                MessageExchangeLifecycle.StreamingResponseToolRequest
        }
    }
}