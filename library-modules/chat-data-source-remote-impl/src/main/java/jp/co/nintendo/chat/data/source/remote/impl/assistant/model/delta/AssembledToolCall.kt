package jp.co.nintendo.chat.data.source.remote.impl.assistant.model.delta

/**
 * A state class representing assembled tool call
 */
class AssembledToolCall {
    var id: String? = null
    var type: String? = null
    var functionName: String? = null
    val functionArguments: StringBuilder = StringBuilder()
}
