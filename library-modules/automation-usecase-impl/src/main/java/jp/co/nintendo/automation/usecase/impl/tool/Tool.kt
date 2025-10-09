package jp.co.nintendo.automation.usecase.impl.tool

import jp.co.nintendo.automation.domain.tool.model.ToolSignature
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecision
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecisionResult

/**
 * An interface representing automation tool for reading/writing/processing in app
 */
interface Tool {
    val toolSignature: ToolSignature
    suspend fun getUserDecision(): UserDecision
    suspend fun handle(
        userDecisionResult: UserDecisionResult,
        toolCallId: String,
        argumentsJson: String
    ): String
}
