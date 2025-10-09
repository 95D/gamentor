package jp.co.nintendo.chat.data.source.remote.impl.assistant.model.delta

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A delta data transfer object for choice
 */
@Serializable
data class ChoiceDelta(
    val index: Int? = null,
    @SerialName("delta")
    val contentDelta: ChoiceContentDelta? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null
) {
    /**
     * A delta data transfer object for content of choice
     */
    @Serializable
    data class ChoiceContentDelta(
        val role: String? = null,
        val content: String? = null,
        @SerialName("tool_calls")
        val toolCalls: List<ToolCallDelta>? = null
    )
}
