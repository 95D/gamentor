package dev.headwind.automation.model.tool

/**
 * A model class representing tool call
 */
data class ToolCall(
    val toolCallId: String,
    val toolName: String,
    val argumentsJson: String
)
