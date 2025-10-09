package jp.co.nintendo.chat.data.source.remote.assistant.model.dto

import kotlinx.serialization.Serializable

/**
 * A data transfer object representing invocation tool from AI Assistant
 */
@Serializable
data class ToolCallDto(
    val id: String,
    val function: FunctionCall,
    val type: String
) {
    @Serializable
    data class FunctionCall(
        val name: String,
        val arguments: String
    )
}
