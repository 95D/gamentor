package jp.co.nintendo.automation.usecase.impl.tool.stateholder

import android.util.Log
import jp.co.nintendo.automation.usecase.impl.tool.model.ToolCallState
import jp.co.nintendo.automation.usecase.impl.tool.usecase.ProcessToolUseCaseImpl
import jp.co.nintendo.automation.domain.tool.model.ToolCall
import jp.co.nintendo.automation.domain.tool.model.ToolReturn
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecisionResult
import jp.co.nintendo.automation.domain.tool.model.lifecycle.ProcessToolLifecycle
import jp.co.nintendo.automation.domain.tool.stateholder.ProcessToolStateHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex

/**
 * An implementation of [ProcessToolStateHolder]
 */
class ProcessToolStateHolderImpl(
    private val processToolUseCaseImpl: ProcessToolUseCaseImpl
) : ProcessToolStateHolder {
    private val processingMutex = Mutex()
    private val toolCallStates = MutableStateFlow<List<ToolCallState>>(emptyList())

    override val processToolLifecycleStateFlow: Flow<ProcessToolLifecycle> =
        toolCallStates.map { states -> computeLifecycle(states) }

    override suspend fun mayProcessToolCalls(channelId: String, toolCalls: List<ToolCall>) {
        if (isRunning()) return
        toolCallStates.update {
            toolCalls.map { ToolCallState.Waiting(it) }
        }
        processSafely { processToolUseCaseImpl.process(it) }
    }

    override suspend fun mayHandleUserDecision(result: UserDecisionResult) {
        processSafely {
            processToolUseCaseImpl.process(
                toolCallStates = it,
                userDecisionResult = result
            )
        }
    }

    private suspend fun processSafely(
        transform: suspend ProcessToolUseCaseImpl.(List<ToolCallState>) -> List<ToolCallState>
    ) {
        if (!processingMutex.tryLock()) return

        val currentToolCallStates = toolCallStates.value
        try {
            toolCallStates.update { processToolUseCaseImpl.transform(currentToolCallStates) }
        } catch (e: Exception) {
            toolCallStates.update {
                currentToolCallStates.map {
                    processToolUseCaseImpl.createCompleteStateByFailure(
                        toolCall = it.toolCall,
                        reason = e::class.simpleName.orEmpty()
                    )
                }
            }
        } finally {
            processingMutex.unlock()
        }
    }


    private fun isRunning(): Boolean = toolCallStates.value.isNotEmpty()

    fun clear() {
        toolCallStates.value = emptyList()
    }

    private fun computeLifecycle(states: List<ToolCallState>): ProcessToolLifecycle {
        if (states.isEmpty()) {
            return ProcessToolLifecycle.Idle
        }
        if (states.all { it is ToolCallState.Complete }) {
            Log.d("ChunkA", states.toString())
            return ProcessToolLifecycle.Done(
                states.filterIsInstance<ToolCallState.Complete>().map {
                    ToolReturn(it.toolCall.toolCallId, it.returnContent)
                }
            )
        }

        val decidingState =
            states.firstOrNull { it is ToolCallState.Deciding } as? ToolCallState.Deciding
        if (decidingState != null) {
            return ProcessToolLifecycle.UserDecisionRequested(decidingState.userDecision)
        }

        val processingState = states.firstOrNull { it !is ToolCallState.Complete }
        return ProcessToolLifecycle.ProcessingTool(processingState?.toolCall?.toolName ?: "")
    }
}
