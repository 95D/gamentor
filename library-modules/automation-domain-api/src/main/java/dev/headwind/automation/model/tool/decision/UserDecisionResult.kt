package dev.headwind.automation.model.tool.decision

/**
 * A sealed interface representing result of user's decision of tool progress
 */
interface UserDecisionResult {
    data object None: UserDecisionResult
    /**
     * A [UserDecisionResult] representing user's approve of tool progress
     */
    data class Approve(val isApproved: Boolean): UserDecisionResult
}
