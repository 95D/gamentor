package jp.co.nintendo.automation.domain.tool.model.lifecycle

import jp.co.nintendo.automation.domain.tool.model.ToolReturn
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecision

/**
 * A sealed interface representing life cycle of tool process task.
 *
 * Represents each step of processing requested tools including handling user decision
 */
sealed interface ProcessToolLifecycle {
    data object Idle : ProcessToolLifecycle
    /**
     * A [ProcessToolLifecycle] indicating the system is executing the required tool
     */
    data class ProcessingTool(val toolName: String): ProcessToolLifecycle

    /**
     * A [ProcessToolLifecycle] signaling that user input is required to proceed
     */
    data class UserDecisionRequested(val userDecision: UserDecision) : ProcessToolLifecycle

    /**
     * A [ProcessToolLifecycle] indicating the entire operation is complete
     */
    data class Done(val returns: List<ToolReturn>) : ProcessToolLifecycle
}
