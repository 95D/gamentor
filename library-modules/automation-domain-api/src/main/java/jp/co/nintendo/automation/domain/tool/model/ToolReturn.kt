package jp.co.nintendo.automation.domain.tool.model

/**
 * A model class representing return content of tool process
 */
data class ToolReturn(
    val toolCallId: String,
    val content: String
)
