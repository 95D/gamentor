package jp.co.nintendo.automation.business.impl.tool.usecase

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import jp.co.nintendo.automation.business.impl.tool.Tool
import jp.co.nintendo.automation.business.impl.tool.ToolFactory
import jp.co.nintendo.automation.model.tool.ToolCall
import jp.co.nintendo.automation.model.tool.ToolProcessLabel
import jp.co.nintendo.automation.model.tool.decision.UserApproveLabel
import jp.co.nintendo.automation.model.tool.decision.UserDecision
import jp.co.nintendo.automation.model.tool.decision.UserDecisionResult
import jp.co.nintendo.automation.business.impl.tool.model.CreateToolResult
import jp.co.nintendo.automation.business.impl.tool.model.ToolCallState
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * An unit test for [ProcessToolUseCaseImpl]
 */
@RunWith(AndroidJUnit4::class)
class ProcessToolUseCaseImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var toolFooFactory: ToolFactory

    @Mock
    private lateinit var toolFoo: Tool

    @Mock
    private lateinit var json: Json

    private lateinit var target: ProcessToolUseCaseImpl

    @Before
    fun setUp() {
        target = ProcessToolUseCaseImpl(
            mapOf("foo" to toolFooFactory),
            json
        )
    }

    @Test
    fun `Create new tool`() = runTest {
        whenever(toolFooFactory.createTool()).doReturn(toolFoo)
        val testToolCall = mock<ToolCall> {
            on { toolName } doReturn "foo"
        }
        assertEquals(
            CreateToolResult.Success(toolFoo),
            target.createTool("MSG_01", testToolCall)
        )
    }

    @Test
    fun `Create new tool failed`() = runTest {
        whenever(json.encodeToString<Map<String, String>>(any(), any()))
            .doReturn("{}")
        val testToolCall = mock<ToolCall> {
            on { toolName } doReturn "bob"
        }
        assertEquals(
            CreateToolResult.Failure(
                ToolCallState.Complete(
                    "MSG_01",
                    testToolCall,
                    "{}"
                )
            ),
            target.createTool("MSG_01", testToolCall)
        )
    }

    @Test
    fun `Process new tool call without decision`() = runTest {
        whenever(toolFoo.labelBeforeStart).doReturn(ToolProcessLabel.RUNNING_TOOL)
        whenever(toolFoo.labelBeforeDecide).doReturn(ToolProcessLabel.RUNNING_TOOL)
        whenever(toolFoo.labelBeforeComplete).doReturn(ToolProcessLabel.RUNNING_TOOL)
        whenever(toolFoo.getUserDecision()).doReturn(UserDecision.None)
        whenever(
            toolFoo.handle(
                userDecisionResult = UserDecisionResult.None,
                toolCallId = "test_call_id",
                argumentsJson = "{}"
            )
        ).doReturn("{\"answer\":true}")
        val testToolCall = mock<ToolCall> {
            on { toolCallId } doReturn "test_call_id"
            on { argumentsJson } doReturn "{}"
        }

        val createdState = target.create("MSG_01", toolFoo, testToolCall)
        assertEquals(
            ToolCallState.Waiting(
                localMessageId = "MSG_01",
                toolCall = testToolCall,
                label = ToolProcessLabel.RUNNING_TOOL
            ),
            createdState
        )

        target.process(toolFoo, createdState).test {
            assertEquals(
                ToolCallState.Running(
                    localMessageId = "MSG_01",
                    toolCall = testToolCall,
                    label = ToolProcessLabel.RUNNING_TOOL,
                    decisionResult = UserDecisionResult.None
                ),
                awaitItem()
            )
            assertEquals(
                ToolCallState.Complete(
                    localMessageId = "MSG_01",
                    toolCall = testToolCall,
                    returnContent = "{\"answer\":true}"
                ),
                awaitItem()
            )
            awaitComplete()
        }
    }

    @Test
    fun `Process new tool call with decision result`() = runTest {
        whenever(toolFoo.labelBeforeStart).doReturn(ToolProcessLabel.RUNNING_TOOL)
        whenever(toolFoo.labelBeforeDecide).doReturn(ToolProcessLabel.RUNNING_TOOL)
        whenever(toolFoo.getUserDecision()).doReturn(
            UserDecision.Approve(UserApproveLabel.READ_GAME_DATA)
        )
        val testToolCall = mock<ToolCall>()
        val createdState = target.create("MSG_01", toolFoo, testToolCall)
        target.process(toolFoo, createdState).test {
            assertEquals(
                ToolCallState.Deciding(
                    localMessageId = "MSG_01",
                    toolCall = testToolCall,
                    userDecision = UserDecision.Approve(UserApproveLabel.READ_GAME_DATA),
                    label = ToolProcessLabel.RUNNING_TOOL
                ),
                awaitItem()
            )
            awaitComplete()
        }
    }

    @Test
    fun `Decide user approve`() = runTest {
        whenever(toolFoo.labelBeforeComplete).doReturn(ToolProcessLabel.RUNNING_TOOL)
        val testToolCall = mock<ToolCall>()
        val decidingToolCallState = ToolCallState.Deciding(
            localMessageId = "MSG_01",
            toolCall = testToolCall,
            userDecision = UserDecision.Approve(UserApproveLabel.READ_GAME_DATA),
            label = ToolProcessLabel.RUNNING_TOOL
        )
        assertEquals(
            ToolCallState.Running(
                localMessageId = "MSG_01",
                toolCall = testToolCall,
                label = ToolProcessLabel.RUNNING_TOOL,
                decisionResult = UserDecisionResult.Approve(isApproved = true)
            ),
            target.decide(
                toolFoo,
                decidingToolCallState,
                UserDecisionResult.Approve(isApproved = true)
            )
        )
    }

    @Test
    fun `Process running tool`() = runTest {
        whenever(
            toolFoo.handle(
                userDecisionResult = UserDecisionResult.Approve(isApproved = true),
                toolCallId = "test_call_id",
                argumentsJson = "{}"
            )
        ).doReturn("{\"answer\":true}")
        val testToolCall = mock<ToolCall> {
            on { toolCallId } doReturn "test_call_id"
            on { argumentsJson } doReturn "{}"
        }

        val runningState = ToolCallState.Running(
            localMessageId = "MSG_01",
            toolCall = testToolCall,
            label = ToolProcessLabel.RUNNING_TOOL,
            decisionResult = UserDecisionResult.Approve(isApproved = true)
        )

        target.process(toolFoo, runningState).test {
            assertEquals(
                ToolCallState.Complete(
                    localMessageId = "MSG_01",
                    toolCall = testToolCall,
                    returnContent = "{\"answer\":true}"
                ),
                awaitItem()
            )
            awaitComplete()
        }
    }
}
