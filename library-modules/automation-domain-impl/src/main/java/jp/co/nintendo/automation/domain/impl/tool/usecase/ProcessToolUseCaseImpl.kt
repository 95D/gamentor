package jp.co.nintendo.automation.domain.impl.tool.usecase

import jakarta.inject.Inject
import jp.co.nintendo.automation.domain.impl.di.AutomationDomainCommon
import jp.co.nintendo.automation.domain.impl.tool.Tool
import jp.co.nintendo.automation.domain.impl.tool.model.ToolCallState
import jp.co.nintendo.automation.domain.tool.model.ToolCall
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecision
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecisionResult
import kotlinx.serialization.json.Json

/**
 * An use case for processing tool according to [ToolCallState]
 */
class ProcessToolUseCaseImpl @Inject constructor(
    private val toolMap: Map<String, @JvmSuppressWildcards Tool>,
    @param:AutomationDomainCommon private val json: Json
) {
    suspend fun process(
        toolCallStates: List<ToolCallState>,
        userDecisionResult: UserDecisionResult
    ): List<ToolCallState> {
        val decidingTool = toolCallStates.getDecidingTool() ?: return toolCallStates
        return toolCallStates.executeToolWithDecision(decidingTool.toolCall, userDecisionResult)
            .updateNextToolCall()
    }

    suspend fun process(toolCallStates: List<ToolCallState>): List<ToolCallState> =
        toolCallStates.updateNextToolCall()

    private suspend fun List<ToolCallState>.updateNextToolCall(): List<ToolCallState> {
        // 1. Check complete
        if (isAllComplete()) {
            return this
        }

        // 2. Get next tool
        val waitingToolState = getNextWaitingTool() ?: return this
        val toolCall = waitingToolState.toolCall

        // 3. Validate tool

        val tool = toolMap[toolCall.toolName]
        if (tool == null) {
            return process(
                updateToolCallState(
                    createFailureStateCouldNotFindTool(toolCall)
                )
            )
        }

        // 4. Check approve
        val userDecision = tool.getUserDecision()
        return when (userDecision) {
            UserDecision.Approve -> {
                updateToolCallState(ToolCallState.Deciding(toolCall, userDecision))
            }

            UserDecision.None -> executeToolWithDecision(
                toolCall,
                UserDecisionResult.None
            )
        }
    }

    private suspend fun List<ToolCallState>.executeToolWithDecision(
        toolCall: ToolCall,
        userDecisionResult: UserDecisionResult
    ): List<ToolCallState> {
        val tool = toolMap[toolCall.toolName]
        if (tool == null) {
            return updateToolCallState(
                createFailureStateCouldNotFindTool(toolCall)
            )
        }

        val result = tool.handle(
            userDecisionResult = userDecisionResult,
            toolCallId = toolCall.toolCallId,
            argumentsJson = toolCall.argumentsJson
        )

        return updateToolCallState(ToolCallState.Complete(toolCall, result))
    }

    private fun createFailureStateCouldNotFindTool(toolCall: ToolCall): ToolCallState.Complete =
        createCompleteStateByFailure(toolCall, reason = "Could not find tool ${toolCall.toolName}")

    fun createCompleteStateByFailure(toolCall: ToolCall, reason: String): ToolCallState.Complete =
        ToolCallState.Complete(
            toolCall,
            json.encodeToString(
                mapOf(CONTENT_KEY_REASON to reason)
            )
        )

    fun List<ToolCallState>.isAllComplete(): Boolean = all { it is ToolCallState.Complete }

    fun List<ToolCallState>.getNextWaitingTool(): ToolCallState.Waiting? =
        firstOrNull { it is ToolCallState.Waiting } as? ToolCallState.Waiting

    fun List<ToolCallState>.getDecidingTool(): ToolCallState.Deciding? =
        firstOrNull { it is ToolCallState.Deciding } as? ToolCallState.Deciding

    private fun List<ToolCallState>.updateToolCallState(
        newState: ToolCallState
    ): List<ToolCallState> = map {
        if (it.toolCall.toolCallId == newState.toolCall.toolCallId) newState else it
    }

    private companion object {
        const val CONTENT_KEY_REASON = "reason"
    }
}
