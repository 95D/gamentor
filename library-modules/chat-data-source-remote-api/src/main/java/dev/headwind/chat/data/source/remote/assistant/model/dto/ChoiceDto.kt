package dev.headwind.chat.data.source.remote.assistant.model.dto

import kotlinx.serialization.Serializable

/**
 * A data transfer object representing candidate answer of AI assistant
 */
@Serializable
data class ChoiceDto(
    val role: String?,
    val content: String,
    val toolCalls: List<ToolCallDto>
)
