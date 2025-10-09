package jp.co.nintendo.automation.domain.impl.tool

import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.nintendo.automation.usecase.impl.tool.model.ToolCallState
import jp.co.nintendo.automation.usecase.impl.tool.stateholder.ProcessToolStateHolderImpl
import jp.co.nintendo.automation.usecase.impl.tool.usecase.ProcessToolUseCaseImpl
import jp.co.nintendo.automation.domain.tool.model.ToolCall
import jp.co.nintendo.automation.domain.tool.model.ToolReturn
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecision
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecisionResult
import jp.co.nintendo.automation.domain.tool.model.lifecycle.ProcessToolLifecycle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import kotlin.test.Test

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
    fun `Process tool calls`() = runTest {
        val mockFlowCollector = mock<FlowCollector<ProcessToolLifecycle>>()
        backgroundScope.launch {
            target.processToolLifecycleStateFlow.collect(mockFlowCollector)
        }
        val testToolCall = mock<ToolCall> {
            on { toolCallId } doReturn "test_call_id"
        }
        whenever(
            processToolUseCaseImpl.process(
                listOf(
                    ToolCallState.Waiting(testToolCall)
                )
            )
        ).doReturn(listOf(ToolCallState.Deciding(testToolCall, UserDecision.Approve)))

        target.mayProcessToolCalls(
            "test_channel",
            listOf(testToolCall)
        )
        runCurrent()

        inOrder(mockFlowCollector) {
            verify(mockFlowCollector).emit(
                ProcessToolLifecycle.UserDecisionRequested(
                    UserDecision.Approve
                )
            )
        }

        whenever(
            processToolUseCaseImpl.process(
                listOf(
                    ToolCallState.Deciding(testToolCall, UserDecision.Approve)
                ),
                UserDecisionResult.Approve(isApproved = true)
            )
        ).doReturn(listOf(ToolCallState.Complete(testToolCall, "{}")))
        target.mayHandleUserDecision(UserDecisionResult.Approve(isApproved = true))
        runCurrent()
        verify(mockFlowCollector).emit(
            ProcessToolLifecycle.Done(
                listOf(ToolReturn(toolCallId = "test_call_id", content = "{}"))
            )
        )
    }

    @Test
    fun `Process new tool calls but it is running`() = runTest {
        val mockFlowCollector = mock<FlowCollector<ProcessToolLifecycle>>()
        backgroundScope.launch {
            target.processToolLifecycleStateFlow.collect(mockFlowCollector)
        }
        val testToolCall = mock<ToolCall>()
        whenever(
            processToolUseCaseImpl.process(
                listOf(
                    ToolCallState.Waiting(testToolCall)
                )
            )
        ).doReturn(listOf(ToolCallState.Deciding(testToolCall, UserDecision.Approve)))

        target.mayProcessToolCalls(
            "test_channel",
            listOf(testToolCall)
        )
        runCurrent()

        val nextTestToolCall = mock<ToolCall>()
        target.mayProcessToolCalls(
            "test_channel",
            listOf(nextTestToolCall)
        )
        runCurrent()

        inOrder(mockFlowCollector) {
            verify(mockFlowCollector).emit(
                ProcessToolLifecycle.UserDecisionRequested(
                    UserDecision.Approve
                )
            )
        }
        verifyNoMoreInteractions(mockFlowCollector)
    }

    @Test
    fun `Failed to process tool calls by exception`() = runTest {
        val mockFlowCollector = mock<FlowCollector<ProcessToolLifecycle>>()
        backgroundScope.launch {
            target.processToolLifecycleStateFlow.collect(mockFlowCollector)
        }
        val testToolCall = mock<ToolCall> {
            on { toolCallId } doReturn "test_call_id"
        }
        whenever(
            processToolUseCaseImpl.createCompleteStateByFailure(
                testToolCall, "SerializationException"
            )
        ).doReturn(
            ToolCallState.Complete(
                testToolCall,
                "\"reason\":SerializationException"
            )
        )
        whenever(
            processToolUseCaseImpl.process(
                listOf(
                    ToolCallState.Waiting(testToolCall)
                )
            )
        ).doThrow(SerializationException())
        target.mayProcessToolCalls(
            "test_channel",
            listOf(testToolCall)
        )
        runCurrent()
        inOrder(mockFlowCollector) {
            verify(mockFlowCollector).emit(
                ProcessToolLifecycle.Done(
                    listOf(
                        ToolReturn("test_call_id", "\"reason\":SerializationException")
                    )
                )
            )
        }
        verifyNoMoreInteractions(mockFlowCollector)
    }

    @Test
    fun `Clear running process tool`() = runTest {
        val mockFlowCollector = mock<FlowCollector<ProcessToolLifecycle>>()
        backgroundScope.launch {
            target.processToolLifecycleStateFlow.collect(mockFlowCollector)
        }
        val testToolCall = mock<ToolCall>()
        whenever(
            processToolUseCaseImpl.process(
                listOf(
                    ToolCallState.Waiting(testToolCall)
                )
            )
        ).doReturn(listOf(ToolCallState.Deciding(testToolCall, UserDecision.Approve)))

        target.mayProcessToolCalls(
            "test_channel",
            listOf(testToolCall)
        )
        runCurrent()
        target.clear()
        runCurrent()

        inOrder(mockFlowCollector) {
            verify(mockFlowCollector).emit(
                ProcessToolLifecycle.UserDecisionRequested(
                    UserDecision.Approve
                )
            )
            verify(mockFlowCollector).emit(
                ProcessToolLifecycle.Idle
            )
        }
        verifyNoMoreInteractions(mockFlowCollector)
    }
}
