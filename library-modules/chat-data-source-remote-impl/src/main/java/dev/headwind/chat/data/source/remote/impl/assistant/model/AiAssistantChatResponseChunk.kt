package dev.headwind.chat.data.source.remote.impl.assistant.model

import dev.headwind.chat.data.source.remote.impl.assistant.model.delta.ChoiceDelta
import kotlinx.serialization.Serializable

/**
 * A delta data transfer object for response
 */
@Serializable
data class AiAssistantChatResponseChunk(
    val id: String,
    val choices: List<ChoiceDelta>
)
