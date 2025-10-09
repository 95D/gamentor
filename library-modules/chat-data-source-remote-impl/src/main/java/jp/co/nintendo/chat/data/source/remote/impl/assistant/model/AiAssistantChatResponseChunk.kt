package jp.co.nintendo.chat.data.source.remote.impl.assistant.model

import jp.co.nintendo.chat.data.source.remote.impl.assistant.model.delta.ChoiceDelta
import kotlinx.serialization.Serializable

/**
 * A delta data transfer object for response
 */
@Serializable
data class AiAssistantChatResponseChunk(
    val id: String,
    val choices: List<ChoiceDelta>
)
