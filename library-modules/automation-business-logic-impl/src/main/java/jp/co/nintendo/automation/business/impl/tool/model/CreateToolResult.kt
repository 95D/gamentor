package jp.co.nintendo.automation.business.impl.tool.model

import jp.co.nintendo.automation.business.impl.tool.Tool

sealed interface CreateToolResult {
    data class Success(val tool: Tool): CreateToolResult
    data class Failure(val toolCallState: ToolCallState): CreateToolResult
}