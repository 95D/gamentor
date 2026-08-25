package dev.headwind.chat.data.source.remote.impl.assistant.model.delta

import kotlinx.serialization.Serializable

/**
 * A delta data transfer object for tool call
 */
@Serializable
data class ToolCallDelta(
    val index: Int? = null,
    val id: String? = null,
    val type: String? = null,
    val function: FunctionDelta? = null
) {
    /**
     * A delta data transfer object for function in tool call
     */
    @Serializable
    data class FunctionDelta(
        val name: String? = null,
        val arguments: String? = null
    )
}
