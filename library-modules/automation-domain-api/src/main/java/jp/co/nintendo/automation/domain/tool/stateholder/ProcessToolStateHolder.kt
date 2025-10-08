package jp.co.nintendo.automation.domain.tool.stateholder

import jp.co.nintendo.automation.domain.tool.model.ToolCall
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecisionResult
import jp.co.nintendo.automation.domain.tool.model.lifecycle.ProcessToolLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * An state-ful orchestrator for processing tool
 *
 * We could create it
 */
interface ProcessToolStateHolder {
    val processToolLifecycleStateFlow: Flow<ProcessToolLifecycle>
    suspend fun mayProcessToolCalls(channelId: String, toolCalls: List<ToolCall>)
    suspend fun mayHandleUserDecision(result: UserDecisionResult)
}
