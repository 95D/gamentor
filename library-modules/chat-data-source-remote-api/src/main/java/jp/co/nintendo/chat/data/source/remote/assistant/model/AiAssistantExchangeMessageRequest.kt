package jp.co.nintendo.chat.data.source.remote.assistant.model

import jp.co.nintendo.chat.data.source.remote.assistant.AiAssistantChatRemoteDataSource
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.MessageLogDto
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ToolCatalogDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A request model to request [AiAssistantChatRemoteDataSource.exchangeMessage]
 */
@Serializable
data class AiAssistantExchangeMessageRequest(
    val model: String,
    @SerialName("messages")
    val messages: List<MessageLogDto>,
    @SerialName("tools")
    val toolCatalogs: List<ToolCatalogDto>,
    @SerialName("tool_choice")
    val toolChoice: String,
    @SerialName("stream")
    val isStreamAnswer: Boolean
)
