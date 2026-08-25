package dev.headwind.chat.data.source.remote.assistant.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data transfer object representing message log in current chat channel
 */
@Serializable
data class MessageLogDto(
    val completionId: String?,
    val role: String,
    @SerialName("tool_call_id")
    val toolCallId: String? = null,
    @SerialName("tool_calls")
    val toolCalls: List<ToolCallDto>? = null,
    val content: String? = null
)
