package jp.co.nintendo.chat.data.source.remote.impl.assistant.model.delta

/**
 * A state class representing assembled choice
 */
class AssembledChoice {
    var role: String? = null
    val content: StringBuilder = StringBuilder()
    val toolCalls: MutableMap<Int, AssembledToolCall> = mutableMapOf()
    var finishReason: String? = null
}
