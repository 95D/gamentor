package jp.co.nintendo.automation.domain.impl.tool

import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.nintendo.automation.usecase.impl.tool.model.ToolCallState
import jp.co.nintendo.automation.usecase.impl.tool.usecase.ProcessToolUseCaseImpl
import jp.co.nintendo.automation.domain.tool.model.ToolCall
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecision
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecisionResult
import jp.co.nintendo.automation.usecase.impl.tool.Tool
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
import org.mockito.kotlin.eq
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
    private lateinit var toolFoo: Tool

    @Mock
    private lateinit var toolBob: Tool

    @Mock
    private lateinit var json: Json

    private lateinit var target: ProcessToolUseCaseImpl

    @Before
    fun setUp() {
        target = ProcessToolUseCaseImpl(
            mapOf("foo" to toolFoo, "bob" to toolBob),
            json
        )
    }

    @Test
    fun `Process without decision`() = runTest {
        whenever(toolFoo.getUserDecision()).doReturn(UserDecision.None)
        whenever(
            toolFoo.handle(UserDecisionResult.None, "test_call_id", "{}")
        ).doReturn("{\"answer\":true}")
        val testToolCall = mock<ToolCall> {
            on { toolCallId } doReturn "test_call_id"
            on { toolName } doReturn "foo"
            on { argumentsJson } doReturn "{}"
        }
        val testToolCallState = ToolCallState.Waiting(testToolCall)
        assertEquals(
            target.process(listOf(testToolCallState)),
            listOf(ToolCallState.Complete(testToolCall, "{\"answer\":true}"))
        )
    }

    @Test
    fun `Process with decision result`() = runTest {
        whenever(toolFoo.getUserDecision()).doReturn(UserDecision.Approve)
        val testToolCall = mock<ToolCall> {
            on { toolName } doReturn "foo"
        }
        val testToolCallState = ToolCallState.Waiting(testToolCall)
        assertEquals(
            target.process(listOf(testToolCallState)),
            listOf(ToolCallState.Deciding(testToolCall, UserDecision.Approve))
        )
    }

    @Test
    fun `Process with error by invalid tool`() = runTest {
        whenever(
            json.encodeToString(
                any(),
                eq(
                    mapOf(
                        "reason" to "Could not find tool xxx"
                    )
                )
            )
        ).doReturn("{\"reason\": Could not find tool xxx}")
        val testToolCall = mock<ToolCall> {
            on { toolName } doReturn "xxx"
        }
        val testToolCallState = ToolCallState.Waiting(testToolCall)
        assertEquals(
            target.process(listOf(testToolCallState)),
            listOf(
                ToolCallState.Complete(
                    testToolCall,
                    "{\"reason\": Could not find tool xxx}"
                )
            )
        )
    }
}
