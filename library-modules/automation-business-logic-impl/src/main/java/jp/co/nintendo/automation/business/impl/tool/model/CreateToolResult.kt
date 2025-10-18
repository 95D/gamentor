package jp.co.nintendo.automation.business.impl.tool.model

import jp.co.nintendo.automation.business.impl.tool.Tool
import jp.co.nintendo.automation.model.tool.ToolCall

/**
 * A request model to create [Tool] for processing [ToolCall]
 */
sealed interface CreateToolResult {
    data class Success(val tool: Tool): CreateToolResult
    data class Failure(val toolCallState: ToolCallState): CreateToolResult
}