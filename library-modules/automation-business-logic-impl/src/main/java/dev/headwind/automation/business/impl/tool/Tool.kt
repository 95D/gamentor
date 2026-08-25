package dev.headwind.automation.business.impl.tool

import dev.headwind.automation.model.tool.ToolProcessLabel
import dev.headwind.automation.model.tool.decision.UserDecision
import dev.headwind.automation.model.tool.decision.UserDecisionResult
import dev.headwind.automation.business.impl.tool.model.ToolCallState

/**
 * An interface representing automation tool for reading/writing/processing in app
 */
interface Tool {
    val labelBeforeStart: ToolProcessLabel get() =  ToolProcessLabel.RUNNING_TOOL
    val labelBeforeDecide: ToolProcessLabel get() =  ToolProcessLabel.RUNNING_TOOL
    val labelBeforeComplete: ToolProcessLabel get() =  ToolProcessLabel.RUNNING_TOOL
    suspend fun getUserDecision(): UserDecision
    suspend fun handle(
        userDecisionResult: UserDecisionResult,
        toolCallId: String,
        argumentsJson: String
    ): String
    suspend fun cancel(toolCallState: ToolCallState)
}
