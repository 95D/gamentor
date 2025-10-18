package jp.co.nintendo.automation.business.tool.stateholder

import jp.co.nintendo.automation.model.tool.ToolCall
import jp.co.nintendo.automation.model.tool.ToolReturn
import jp.co.nintendo.automation.model.tool.decision.UserDecisionResult
import jp.co.nintendo.automation.model.tool.lifecycle.ProcessToolLifecycle
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
