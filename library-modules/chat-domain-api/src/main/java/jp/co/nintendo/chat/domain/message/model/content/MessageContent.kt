package jp.co.nintendo.chat.domain.message.model.content

import jp.co.nintendo.chat.domain.message.model.content.system.SystemMessageErrorType
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Represents the content payload of a chat message.
 * Supports text, tool invocations, and tool execution results.
 */
@Serializable
@SerialName("message_content")
sealed interface MessageContent

/**
 * A [MessageContent] representing plain text.
 */
@Serializable
@SerialName("text_content")
data class TextContent(
    @SerialName("raw_text")
    val rawText: String
) : MessageContent

/**
 * A [MessageContent] representing a request to invoke a tool with specified arguments.
 */
@Serializable
@SerialName("tool_request_content")
data class ToolRequestContent(
    val toolCalls: List<ToolCall>
) : MessageContent {
    @Serializable
    data class ToolCall(
        @SerialName("tool_call_id")
        val toolCallId: String,
        @SerialName("tool_name")
        val toolName: String,
        @SerialName("argument_json")
        val argumentsJson: String
    )
}

/**
 * A [MessageContent] representing the result returned from a tool execution.
 */
@Serializable
@SerialName("tool_return_content")
data class ToolResponseContent(
    val toolReturns: List<ToolReturn>
) : MessageContent {
    @Serializable
    data class ToolReturn(
        @SerialName("tool_call_id")
        val toolCallId: String,
        val content: String
    )
}

@Serializable
@SerialName("system_error_content")
data class SystemErrorContent(
    @SerialName("error_type")
    val errorType: SystemMessageErrorType
)
