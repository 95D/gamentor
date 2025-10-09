package jp.co.nintendo.chat.data.repository.impl.message.assistant.factory

import jp.co.nintendo.automation.domain.tool.usecase.GetToolSignaturesUseCase
import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageRequest
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.MessageLogDto
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ToolCallDto
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ToolCatalogDto
import jp.co.nintendo.chat.domain.assistant.AiAssistantChatRole
import jp.co.nintendo.chat.domain.message.model.ChatMessage
import jp.co.nintendo.chat.domain.message.model.content.MessageContent
import jp.co.nintendo.chat.domain.message.model.content.TextContent
import jp.co.nintendo.chat.domain.message.model.content.ToolRequestContent
import jp.co.nintendo.chat.domain.message.model.content.ToolResponseContent
import jp.co.nintendo.chat.domain.message.model.extras.AiAssistantExtras
import jp.co.nintendo.chat.domain.message.model.extras.AppOwnerExtras
import jp.co.nintendo.chat.domain.message.model.extras.MessageSenderExtras
import jp.co.nintendo.chat.domain.message.model.extras.SystemExtras
import kotlinx.serialization.json.JsonElement
import javax.inject.Inject

/**
 * A factory class for creating [AiAssistantExchangeMessageRequest]
 */
class AiAssistantChatRequestFactory  @Inject constructor(
    getToolSignaturesUseCase: GetToolSignaturesUseCase
) {
    private val toolCatalogDtoList: List<ToolCatalogDto> = getToolSignaturesUseCase
        .getAllToolSignaturesJson()
        .map(this::createToolCatalogDto)

    fun create(messages: List<ChatMessage>): AiAssistantExchangeMessageRequest =
        AiAssistantExchangeMessageRequest(
            model = "gpt-4.1-mini",
            messages = messages.map(this::createMessageLogDto).flatten(),
            toolCatalogs = toolCatalogDtoList,
            toolChoice = "auto",
            isStreamAnswer = true
        )

    private fun createMessageLogDto(message: ChatMessage): List<MessageLogDto> {
        val completionId = getCompletionId(message.senderExtras)
        val role = getRoleName(message.senderExtras, message.content)
        return createMessageLogDto(completionId, role, message.content)
    }

    private fun createMessageLogDto(
        completionId: String?,
        role: String,
        content: MessageContent
    ): List<MessageLogDto> = when (content) {
        is TextContent -> listOf(
            MessageLogDto(completionId, role, content = content.rawText)
        )

        is ToolRequestContent -> listOf(
            MessageLogDto(
                completionId,
                role,
                toolCalls = content.toolCalls.map(
                    this@AiAssistantChatRequestFactory::createToolCallDto
                ),
            )
        )

        is ToolResponseContent -> content.toolReturns.map {
            MessageLogDto(
                completionId,
                role,
                toolCallId = it.toolCallId,
                content = it.content
            )
        }
    }

    private fun getCompletionId(senderExtras: MessageSenderExtras): String? =
        (senderExtras as? AiAssistantExtras)?.responseId

    private fun createToolCallDto(
        toolCall: ToolRequestContent.ToolCall
    ): ToolCallDto = ToolCallDto(
        id = toolCall.toolCallId,
        type = TYPE_NAME_FUNCTION,
        function = ToolCallDto.FunctionCall(
            name = toolCall.toolName,
            arguments = toolCall.argumentsJson
        )
    )

    private fun getRoleName(
        senderExtras: MessageSenderExtras,
        content: MessageContent
    ): String = when {
        content is ToolResponseContent -> AiAssistantChatRole.TOOL
        senderExtras == SystemExtras -> AiAssistantChatRole.SYSTEM
        senderExtras == AppOwnerExtras -> AiAssistantChatRole.USER
        senderExtras is AiAssistantExtras -> AiAssistantChatRole.AI_ASSISTANT
        else -> AiAssistantChatRole.UNKNOWN
    }.roleName

    private fun createToolCatalogDto(toolSignatureJson: JsonElement): ToolCatalogDto =
        ToolCatalogDto(type = TYPE_NAME_FUNCTION, function = toolSignatureJson)

    companion object {
        const val TYPE_NAME_FUNCTION = "function"
    }
}
