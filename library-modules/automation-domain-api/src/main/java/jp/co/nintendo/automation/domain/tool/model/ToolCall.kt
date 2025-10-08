package jp.co.nintendo.automation.domain.tool.model

/**
 * A model class representing tool call
 */
data class ToolCall(
    val toolCallId: String,
    val toolName: String,
    val argumentsJson: String
)
