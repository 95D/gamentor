package dev.headwind.automation.business.impl.tool.model

import dev.headwind.automation.business.impl.tool.Tool
import dev.headwind.automation.model.tool.ToolCall

/**
 * A request model to create [Tool] for processing [ToolCall]
 */
sealed interface CreateToolResult {
    data class Success(val tool: Tool): CreateToolResult
    data class Failure(val toolCallState: ToolCallState): CreateToolResult
}