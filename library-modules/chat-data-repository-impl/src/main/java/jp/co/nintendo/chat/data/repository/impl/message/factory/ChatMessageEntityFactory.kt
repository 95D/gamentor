package jp.co.nintendo.chat.data.repository.impl.message.factory

import jp.co.nintendo.chat.data.repository.impl.di.ChatDataRepositoryCommon
import jp.co.nintendo.chat.data.repository.impl.time.SystemCurrentMillisCalculator
import jp.co.nintendo.chat.data.source.local.message.entity.ChatMessageEntity
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ChoiceDto
import jp.co.nintendo.chat.domain.message.model.content.MessageContent
import jp.co.nintendo.chat.domain.message.model.content.TextContent
import jp.co.nintendo.chat.domain.message.model.content.ToolRequestContent
import jp.co.nintendo.chat.domain.message.model.extras.AiAssistantExtras
import jp.co.nintendo.chat.domain.message.model.extras.MessageSenderExtras
import jp.co.nintendo.id.domain.factory.EntityIdFactory
import jp.co.nintendo.id.domain.model.DomainCode
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
        ToolRequestContent(
            choice.toolCalls.map {
                ToolRequestContent.ToolCall(
                    it.id,
                    it.function.name,
                    it.function.arguments
                )
            }
        )
    }
}

