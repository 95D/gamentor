package jp.co.nintendo.automation.domain.impl.tool

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import jp.co.nintendo.automation.domain.tool.model.ToolCall
import jp.co.nintendo.automation.domain.tool.model.ToolProcessLabel
import jp.co.nintendo.automation.domain.tool.model.ToolReturn
import jp.co.nintendo.automation.domain.tool.model.decision.UserApproveLabel
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecision
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecisionResult
import jp.co.nintendo.automation.domain.tool.model.lifecycle.ProcessToolLifecycle
import jp.co.nintendo.automation.usecase.impl.tool.Tool
import jp.co.nintendo.automation.usecase.impl.tool.model.CreateToolResult
import jp.co.nintendo.automation.usecase.impl.tool.model.ToolCallState
import jp.co.nintendo.automation.usecase.impl.tool.stateholder.ProcessToolStateHolderImpl
import jp.co.nintendo.automation.usecase.impl.tool.usecase.ProcessToolUseCaseImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * An unit test for [ProcessToolStateHolderImpl]
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ProcessToolStateHolderImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var processToolUseCaseImpl: ProcessToolUseCaseImpl

    private lateinit var target: ProcessToolStateHolderImpl

    @Before
    fun setUp() {
        target = ProcessToolStateHolderImpl(
            processToolUseCaseImpl
        )
    }

    @Test
    fun `Process already complete`() = runTest {
        val testToolCall = mock<ToolCall> {
            on { toolCallId } doReturn "test_call_id"
        }
        val testToolReturn = mock<ToolReturn> {
            on { toolCallId } doReturn "test_call_id"
        }
        target.mayProcessToolCalls(
            "test_channel",
            "MSG_01",
            listOf(testToolCall),
            listOf(testToolReturn)
        )
        verifyNoInteractions(processToolUseCaseImpl)
    }

    @Test
    fun `Process tool calls`() = runTest {
        val testTool = mock<Tool>()
        val testToolCall = mock<ToolCall> {
            on { toolCallId } doReturn "test_call_id"
        }
        whenever(
            processToolUseCaseImpl.createTool("MSG_01", testToolCall)
        ).doReturn(CreateToolResult.Success(testTool))

        val testWaitingToolCallState = ToolCallState.Waiting(
            localMessageId = "MSG_01",
            toolCall = testToolCall, label = ToolProcessLabel.RUNNING_TOOL
        )
        val testDecidingToolCallState = ToolCallState.Deciding(
            localMessageId = "MSG_01",
            toolCall = testToolCall,
            label = ToolProcessLabel.RUNNING_TOOL,
            userDecision = UserDecision.Approve(UserApproveLabel.READ_GAME_DATA)
        )
        val testRunningToolCallState = ToolCallState.Running(
            localMessageId = "MSG_01",
            toolCall = testToolCall,
            label = ToolProcessLabel.RUNNING_TOOL,
            decisionResult = UserDecisionResult.Approve(isApproved = true)
        )
        val testCompleteToolCallState = ToolCallState.Complete(
            localMessageId = "MSG_01",
            toolCall = testToolCall,
            returnContent = "{}"
        )

        whenever(
            processToolUseCaseImpl.create(
                localMessageId = "MSG_01",
                tool = testTool,
                toolCall = testToolCall,
            )
        ).doReturn(testWaitingToolCallState)
        whenever(
            processToolUseCaseImpl.process(
                tool = testTool,
                toolCallState = testWaitingToolCallState
            )
        ).doReturn(flowOf(testDecidingToolCallState))

        whenever(
            processToolUseCaseImpl.decide(
                tool = testTool,
                testDecidingToolCallState,
                UserDecisionResult.Approve(isApproved = true)
            )
        ).doReturn(testRunningToolCallState)
        whenever(
            processToolUseCaseImpl.process(
                tool = testTool,
                toolCallState = testRunningToolCallState
            )
        ).doReturn(flowOf(testCompleteToolCallState))

        target.processToolLifecycleStateFlow.test {
            assertEquals(
                ProcessToolLifecycle.Idle,
                awaitItem()
            )
            target.mayProcessToolCalls(
                channelId = "test_channel",
                localMessageId = "MSG_01",
                toolCalls = listOf(testToolCall),
                toolReturns = emptyList()
            )
            runCurrent()
            assertEquals(
                ProcessToolLifecycle.Process(label = ToolProcessLabel.RUNNING_TOOL),
                awaitItem()
            )
            assertEquals(
                ProcessToolLifecycle.BlockedByUserDecision(
                    label = ToolProcessLabel.RUNNING_TOOL,
                    userDecision = UserDecision.Approve(UserApproveLabel.READ_GAME_DATA)
                ),
                awaitItem()
            )

            target.mayHandleUserDecision(UserDecisionResult.Approve(isApproved = true))
            runCurrent()
            assertEquals(
                ProcessToolLifecycle.Process(label = ToolProcessLabel.RUNNING_TOOL),
                awaitItem()
            )
            assertEquals(
                ProcessToolLifecycle.Done(
                    localMessageId = "MSG_01",
                    toolReturn = ToolReturn(toolCallId = "test_call_id", content = "{}")
                ),
                awaitItem()
            )
        }
    }
}
