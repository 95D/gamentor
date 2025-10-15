package jp.co.nintendo.automation.usecase.impl.tool

import jp.co.nintendo.automation.domain.tool.model.ToolProcessLabel
import jp.co.nintendo.automation.domain.tool.model.ToolSignature
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecision
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecisionResult
import jp.co.nintendo.automation.usecase.impl.tool.model.ToolCallState

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
