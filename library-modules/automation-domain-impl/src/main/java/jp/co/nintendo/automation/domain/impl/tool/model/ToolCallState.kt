package jp.co.nintendo.automation.domain.impl.tool.model

import jp.co.nintendo.automation.domain.tool.model.decision.UserDecision
import jp.co.nintendo.automation.domain.tool.model.ToolCall

/**
 * A state model representing states of processing of one tool call
 */
sealed interface ToolCallState {
    val toolCall: ToolCall

    data class Waiting(override val toolCall: ToolCall) : ToolCallState
    data class Deciding(override val toolCall: ToolCall, val userDecision: UserDecision) :
        ToolCallState

    data class Complete(override val toolCall: ToolCall, val returnContent: String) : ToolCallState
}
