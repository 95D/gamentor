package jp.co.nintendo.automation.business.impl.tool.usecase

import jakarta.inject.Inject
import jp.co.nintendo.automation.model.tool.ToolCall
import jp.co.nintendo.automation.model.tool.decision.UserDecision
import jp.co.nintendo.automation.model.tool.decision.UserDecisionResult
import jp.co.nintendo.automation.business.impl.di.AutomationDomainCommon
import jp.co.nintendo.automation.business.impl.tool.Tool
import jp.co.nintendo.automation.business.impl.tool.ToolFactory
import jp.co.nintendo.automation.business.impl.tool.model.CreateToolResult
import jp.co.nintendo.automation.business.impl.tool.model.ToolCallState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/**
 * An use case for processing tool according to [ToolCallState]
 */
class ProcessToolUseCaseImpl @Inject constructor(
    private val toolFactoryMap: Map<String, @JvmSuppressWildcards ToolFactory>,
    @param:AutomationDomainCommon private val json: Json
) {
    fun createTool(localMessageId: String, toolCall: ToolCall): CreateToolResult {
        val toolFactory = toolFactoryMap[toolCall.toolName]
        return if (toolFactory == null) {
           CreateToolResult.Failure(
                createFailureStateCouldNotFindTool(
                    localMessageId,
                    toolCall
                )
            )
        } else {
            CreateToolResult.Success(toolFactory.createTool())
        }
    }

    fun create(localMessageId: String, tool: Tool, toolCall: ToolCall): ToolCallState {
        return ToolCallState.Waiting(
            localMessageId,
            toolCall,
            tool.labelBeforeStart
        )
    }

    fun process(tool: Tool, toolCallState: ToolCallState?): Flow<ToolCallState?> = flow {
        var nextToolCallState: ToolCallState? = toolCallState ?: return@flow
        if (nextToolCallState is ToolCallState.Waiting) {
            nextToolCallState = nextToolCallState.start(tool)
            emit(nextToolCallState)
        }

        if (nextToolCallState is ToolCallState.Running) {
            nextToolCallState = nextToolCallState.handle(tool)
            emit(nextToolCallState)
        }
    }

    fun decide(
        tool: Tool,
        toolCallState: ToolCallState?,
        userDecisionResult: UserDecisionResult
    ): ToolCallState? =
        (toolCallState as? ToolCallState.Deciding)?.decide(tool, userDecisionResult) ?: toolCallState

    fun createCompleteStateByFailure(state: ToolCallState, reason: String): ToolCallState.Complete =
        createCompleteStateByFailure(
            localMessageId = state.localMessageId,
            toolCall = state.toolCall,
            reason = reason
        )

    private fun createCompleteStateByFailure(
        localMessageId: String,
        toolCall: ToolCall,
        reason: String
    ): ToolCallState.Complete = ToolCallState.Complete(
        localMessageId,
        toolCall,
        json.encodeToString(
            mapOf(CONTENT_KEY_REASON to reason)
        )
    )

    fun createFailureStateCouldNotFindTool(
        localMessageId: String,
        toolCall: ToolCall
    ): ToolCallState.Complete =
        createCompleteStateByFailure(
            localMessageId = localMessageId,
            toolCall = toolCall,
            reason = "Could not find tool ${toolCall.toolName}"
        )

    private suspend fun ToolCallState.Waiting.start(tool: Tool): ToolCallState {
        val decidingState = ToolCallState.Deciding(
            localMessageId,
            toolCall,
            userDecision = tool.getUserDecision(),
            label = tool.labelBeforeDecide
        )
        return when (decidingState.userDecision) {
            is UserDecision.Approve -> decidingState
            UserDecision.None -> decidingState.decide(
                tool = tool,
                userDecisionResult = UserDecisionResult.None
            )
        }
    }

    private fun ToolCallState.Deciding.decide(
        tool: Tool,
        userDecisionResult: UserDecisionResult
    ): ToolCallState = ToolCallState.Running(
            localMessageId,
            toolCall,
            label = tool.labelBeforeComplete,
            decisionResult = userDecisionResult
        )

    private suspend fun ToolCallState.Running.handle(tool: Tool): ToolCallState {
        val result = tool.handle(
            userDecisionResult = decisionResult,
            toolCallId = toolCall.toolCallId,
            argumentsJson = toolCall.argumentsJson
        )
        return ToolCallState.Complete(localMessageId, toolCall, result)
    }

    private companion object {
        const val CONTENT_KEY_REASON = "reason"
    }
}
