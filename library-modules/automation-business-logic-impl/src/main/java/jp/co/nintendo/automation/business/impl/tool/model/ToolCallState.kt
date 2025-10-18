package jp.co.nintendo.automation.business.impl.tool.model

import jp.co.nintendo.automation.model.tool.ToolCall
import jp.co.nintendo.automation.model.tool.ToolProcessLabel
import jp.co.nintendo.automation.model.tool.decision.UserDecision
import jp.co.nintendo.automation.model.tool.decision.UserDecisionResult

/**
 * A state model representing states of processing of one tool call
 */
sealed interface ToolCallState {
    val localMessageId: String
    val toolCall: ToolCall
    data class Waiting(
        override val localMessageId: String,
        override val toolCall: ToolCall,
        val label: ToolProcessLabel
    ) : ToolCallState

    data class Deciding(
        override val localMessageId: String,
        override val toolCall: ToolCall,
        val label: ToolProcessLabel,
        val userDecision: UserDecision
    ) : ToolCallState

    data class Running(
        override val localMessageId: String,
        override val toolCall: ToolCall,
        val label: ToolProcessLabel,
        val decisionResult: UserDecisionResult
    ) : ToolCallState

    data class Complete(
        override val localMessageId: String,
        override val toolCall: ToolCall,
        val returnContent: String
    ) : ToolCallState
}
