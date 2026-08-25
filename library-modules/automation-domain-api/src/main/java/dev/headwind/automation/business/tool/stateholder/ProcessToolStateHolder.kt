package dev.headwind.automation.business.tool.stateholder

import dev.headwind.automation.model.tool.ToolCall
import dev.headwind.automation.model.tool.ToolReturn
import dev.headwind.automation.model.tool.decision.UserDecisionResult
import dev.headwind.automation.model.tool.lifecycle.ProcessToolLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * An state-ful orchestrator for processing tool
 *
 * We could create it
 */
interface ProcessToolStateHolder {
    val processToolLifecycleStateFlow: Flow<ProcessToolLifecycle>
    suspend fun mayProcessToolCalls(
        channelId: String,
        localMessageId: String,
        toolCalls: List<ToolCall>,
        toolReturns: List<ToolReturn>
    )

    suspend fun mayHandleUserDecision(result: UserDecisionResult)
    suspend fun finishTool(isCommitSuccess: Boolean)
}
