package dev.headwind.automation.model.tool.decision

/**
 * A sealed interface representing user's decision of tool progress
 */
sealed interface UserDecision {
    /**
     * A [UserDecision] representing nothing
     */
    data object None : UserDecision

    /**
     * A [UserDecision] representing user's approve of tool progress
     */
    data class Approve(val label: UserApproveLabel) : UserDecision
}
