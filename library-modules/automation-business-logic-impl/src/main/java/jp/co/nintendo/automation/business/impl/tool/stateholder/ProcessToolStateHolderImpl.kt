package jp.co.nintendo.automation.business.impl.tool.stateholder

import jp.co.nintendo.automation.model.tool.ToolCall
import jp.co.nintendo.automation.model.tool.ToolReturn
import jp.co.nintendo.automation.model.tool.decision.UserDecisionResult
import jp.co.nintendo.automation.model.tool.lifecycle.ProcessToolLifecycle
import jp.co.nintendo.automation.model.tool.lifecycle.ProcessToolLifecycle.BlockedByUserDecision
import jp.co.nintendo.automation.model.tool.lifecycle.ProcessToolLifecycle.Done
import jp.co.nintendo.automation.model.tool.lifecycle.ProcessToolLifecycle.Idle
import jp.co.nintendo.automation.model.tool.lifecycle.ProcessToolLifecycle.Process
import jp.co.nintendo.automation.business.tool.stateholder.ProcessToolStateHolder
import jp.co.nintendo.automation.business.impl.tool.Tool
import jp.co.nintendo.automation.business.impl.tool.model.CreateToolResult
import jp.co.nintendo.automation.business.impl.tool.model.ToolCallState
import jp.co.nintendo.automation.business.impl.tool.usecase.ProcessToolUseCaseImpl
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

/**
 * An implementation of [ProcessToolStateHolder]
 */
class ProcessToolStateHolderImpl(
    private val processToolUseCaseImpl: ProcessToolUseCaseImpl
) : ProcessToolStateHolder {
    private val coroutineScope: CoroutineScope = CoroutineScope(
        Dispatchers.Default + SupervisorJob()
    )
    private var processingJob: Job? = null
    private val processingMutex: Mutex = Mutex()

    private var processingTool: Tool? = null
    private val toolCallState = MutableStateFlow<ToolCallState?>(null)

    override val processToolLifecycleStateFlow: Flow<ProcessToolLifecycle> =
        toolCallState.map { states -> computeLifecycle(states) }

    override suspend fun mayProcessToolCalls(
        channelId: String,
        localMessageId: String,
        toolCalls: List<ToolCall>,
        toolReturns: List<ToolReturn>
    ) {
        val completeToolCallIds = toolReturns.map { it.toolCallId }
        val toolCallToPerform = toolCalls.firstOrNull {
            !completeToolCallIds.contains(it.toolCallId)
        } ?: return
        cancelCurrentProcess()
        val result = processToolUseCaseImpl.createTool(localMessageId, toolCallToPerform)
        when (result) {
            is CreateToolResult.Failure -> {
                toolCallState.value = result.toolCallState
            }

            is CreateToolResult.Success -> {
                processingTool = result.tool
                toolCallState.value = processToolUseCaseImpl.create(
                    localMessageId,
                    tool = result.tool,
                    toolCall = toolCallToPerform
                )
                processSafely {
                    processToolUseCaseImpl.process(
                        result.tool,
                        toolCallState.value
                    ).collect { nextState ->
                        toolCallState.value = nextState
                    }
                }
            }
        }
    }

    private suspend fun cancelCurrentProcess() {
        toolCallState.value?.let {
            processingTool?.cancel(it)
            processingJob?.cancel()
        }
        disposeCurrentToolCall()
    }

    override suspend fun mayHandleUserDecision(result: UserDecisionResult) {
        processSafely {
            val nonNullCurrentCallState = toolCallState.value ?: return@processSafely
            val currentTool = processingTool
            if (currentTool == null) {
                toolCallState.value = processToolUseCaseImpl.createFailureStateCouldNotFindTool(
                    nonNullCurrentCallState.localMessageId,
                    nonNullCurrentCallState.toolCall
                )
                return@processSafely
            }
            val decidedToolCallState = processToolUseCaseImpl.decide(
                tool = currentTool,
                toolCallState = nonNullCurrentCallState,
                userDecisionResult = result
            )
            toolCallState.value = decidedToolCallState
            processToolUseCaseImpl.process(currentTool, decidedToolCallState).collect { nextState ->
                toolCallState.value = nextState
            }
        }
    }

    override suspend fun finishTool(isCommitSuccess: Boolean) {
        if (!isCommitSuccess) {
            toolCallState.value?.let { processingTool?.cancel(it) }
        }
        disposeCurrentToolCall()
    }

    private fun disposeCurrentToolCall() {
        processingTool = null
        toolCallState.value = null
    }

    private fun processSafely(
        transform: suspend () -> Unit
    ) {
        if (!processingMutex.tryLock()) return

        val currentToolCallState = toolCallState.value
        if (currentToolCallState == null) {
            processingMutex.unlock()
            return
        }
        processingJob = coroutineScope.launch {
            try {
                transform()
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                toolCallState.value = processToolUseCaseImpl.createCompleteStateByFailure(
                    state = currentToolCallState,
                    reason = e::class.simpleName.orEmpty()
                )
            } finally {
                processingMutex.unlock()
            }
        }
    }

    private fun computeLifecycle(state: ToolCallState?): ProcessToolLifecycle {
        return when (state) {
            null -> Idle
            is ToolCallState.Waiting -> Process(state.label)
            is ToolCallState.Deciding -> BlockedByUserDecision(
                label = state.label,
                userDecision = state.userDecision
            )

            is ToolCallState.Running -> Process(state.label)
            is ToolCallState.Complete -> Done(
                localMessageId = state.localMessageId,
                toolReturn = ToolReturn(
                    toolCallId = state.toolCall.toolCallId,
                    content = state.returnContent
                )
            )
        }
    }
}
