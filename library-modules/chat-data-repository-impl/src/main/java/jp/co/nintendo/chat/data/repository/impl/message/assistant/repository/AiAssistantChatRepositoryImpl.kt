package jp.co.nintendo.chat.data.repository.impl.message.assistant.repository

import androidx.paging.PagingData
import androidx.paging.map
import jp.co.nintendo.chat.data.repository.impl.message.mapper.ChatMessageMapper
import jp.co.nintendo.chat.data.repository.impl.message.assistant.factory.AiAssistantChatRequestFactory
import jp.co.nintendo.chat.data.repository.impl.message.factory.ChatMessageEntityFactory
import jp.co.nintendo.chat.data.source.local.message.ChatMessageLocalDataSource
import jp.co.nintendo.chat.data.source.local.message.entity.ChatMessageEntity
import jp.co.nintendo.chat.data.source.local.message.model.ChatMessageInsertResult
import jp.co.nintendo.chat.data.source.remote.assistant.AiAssistantChatRemoteDataSource
import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageResponse
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ChoiceDto
import jp.co.nintendo.chat.domain.message.model.ChatMessage
import jp.co.nintendo.chat.domain.message.model.ChatMessageRequest
import jp.co.nintendo.chat.domain.message.model.lifecycle.MessageExchangeLifecycle
import jp.co.nintendo.chat.domain.message.model.paging.MessagePageAnchor
import jp.co.nintendo.chat.domain.message.repository.ChatMessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

/**
 * An implementation of [ChatMessageRepository] for AI Assistant chat
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

    override suspend fun loadMessagePage(
        channelId: String,
        anchor: MessagePageAnchor
    ): Flow<PagingData<ChatMessage>> =
        loadMessagePage(channelId = channelId, initialKey = getInitialKey(channelId, anchor))

    private fun loadMessagePage(channelId: String, initialKey: Int): Flow<PagingData<ChatMessage>> =
        messageLocalDataSource.selectMessagePagingSource(channelId, initialKey)
            .map { it.map(chatMessageMapper::mapToDomain) }


    override suspend fun selectMessage(localMessageId: String): ChatMessage? =
        messageLocalDataSource.selectMessage(localMessageId)?.let(chatMessageMapper::mapToDomain)

    private suspend fun getInitialKey(
        channelId: String,
        anchor: MessagePageAnchor
    ): Int = when (anchor) {
        is MessagePageAnchor.Around -> {
            val entity = messageLocalDataSource.selectMessage(anchor.localMessageId)
            if (entity == null) {
                0
            } else {
                messageLocalDataSource.countNewerOrEqual(
                    channelId,
                    anchorLocalMessageId = entity.localMessageId,
                    anchorCreatedAt = entity.createdAtMillis
                )
            }
        }

        MessagePageAnchor.Latest -> 0
    }

    override suspend fun exchangeMessage(
        channelId: String,
        messageRequest: ChatMessageRequest
    ): Flow<MessageExchangeLifecycle> = flow {
        val sentMessageEntity = chatMessageEntityFactory.create(
            channelId = channelId,
            content = messageRequest.messageContent,
            senderExtras = messageRequest.senderExtras
        )
        emit(MessageExchangeLifecycle.Sending(sentMessageEntity.localMessageId))

        val nullableFailure = commitMessage(sentMessageEntity)
        if (nullableFailure != null) {
            emit(nullableFailure)
            return@flow
        }

        val currentMessages = messageLocalDataSource.selectLatestMessages(channelId, 50)
            .map(chatMessageMapper::mapToDomain)

        emitAll(
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
                    firstChoice.assembledContent
                )

            AiAssistantExchangeMessageResponse.InProgress.ChoiceAssembleSnapshot.ToolCall ->
                MessageExchangeLifecycle.StreamingResponseToolRequest
        }
    }
}
