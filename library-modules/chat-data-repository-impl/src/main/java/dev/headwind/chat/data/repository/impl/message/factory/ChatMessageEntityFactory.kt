package dev.headwind.chat.data.repository.impl.message.factory

import dev.headwind.chat.data.repository.impl.di.ChatDataRepositoryCommon
import dev.headwind.chat.data.repository.impl.time.SystemCurrentMillisCalculator
import dev.headwind.chat.data.source.local.message.entity.ChatMessageEntity
import dev.headwind.chat.data.source.remote.assistant.model.dto.ChoiceDto
import dev.headwind.chat.model.message.ChatMessage
import dev.headwind.chat.model.message.content.MessageContent
import dev.headwind.chat.model.message.content.TextContent
import dev.headwind.chat.model.message.content.ToolProcessContent
import dev.headwind.chat.model.message.extras.AiAssistantExtras
import dev.headwind.chat.model.message.extras.MessageSenderExtras
import dev.headwind.id.domain.factory.EntityIdFactory
import dev.headwind.id.domain.model.DomainCode
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * A factory class for creating new [ChatMessageEntity]
 * in order to request insert it into local data source
 */
class ChatMessageEntityFactory @Inject constructor(
    @param:ChatDataRepositoryCommon private val json: Json,
    private val systemCurrentMillisCalculator: SystemCurrentMillisCalculator,
    private val idFactory: EntityIdFactory
) {
    fun create(
        channelId: String,
        content: MessageContent,
        senderExtras: MessageSenderExtras
    ): ChatMessageEntity =
        ChatMessageEntity(
            localMessageId = idFactory.create(DomainCode.ChatMessage),
            channelId = channelId,
            createdAtMillis = systemCurrentMillisCalculator.getCurrentMillis(),
            contentJson = json.encodeToString(content),
            senderExtrasJson = json.encodeToString(senderExtras)
        )

    fun create(
        channelId: String,
        message: ChatMessage
    ): ChatMessageEntity = ChatMessageEntity(
        localMessageId = message.localMessageId,
        channelId = channelId,
        createdAtMillis = message.createdAtMillis,
        contentJson = json.encodeToString(message.content),
        senderExtrasJson = json.encodeToString(message.senderExtras)
    )

    fun createAiAssistantResponseMessage(
        channelId: String,
        responseId: String,
        choice: ChoiceDto
    ): ChatMessageEntity {
        val messageContent = createMessageContent(choice)
        val senderExtras = AiAssistantExtras(responseId)
        return create(
            channelId = channelId,
            content = messageContent,
            senderExtras = senderExtras
        )
    }

    private fun createMessageContent(
        choice: ChoiceDto
    ): MessageContent = if (choice.toolCalls.isEmpty()) {
        TextContent(choice.content)
    } else {
        ToolProcessContent(
            toolCalls = choice.toolCalls.map {
                ToolProcessContent.ToolCall(
                    it.id,
                    it.function.name,
                    it.function.arguments
                )
            },
            toolReturns = emptyList()
        )
    }
}

