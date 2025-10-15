package jp.co.nintendo.automation.usecase.impl.tool.model

import jp.co.nintendo.automation.usecase.impl.tool.Tool

sealed interface CreateToolResult {
    data class Success(val tool: Tool): CreateToolResult
    data class Failure(val toolCallState: ToolCallState): CreateToolResult
}