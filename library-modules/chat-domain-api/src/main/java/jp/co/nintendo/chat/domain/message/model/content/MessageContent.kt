package jp.co.nintendo.chat.domain.message.model.content

/**
 * Represents the content payload of a chat message.
 * Supports text, tool invocations, and tool execution results.
 */
sealed interface MessageContent {
    /**
     * A [MessageContent] representing plain text.
     */
    data class Text(val rawText: String) : MessageContent
}
