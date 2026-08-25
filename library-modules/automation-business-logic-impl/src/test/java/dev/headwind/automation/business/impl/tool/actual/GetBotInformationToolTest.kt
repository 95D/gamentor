package dev.headwind.automation.business.impl.tool.actual

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.headwind.automation.model.tool.decision.UserDecision
import dev.headwind.automation.model.tool.decision.UserDecisionResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * An unit test for [GetBotInformationTool]
 */
@RunWith(AndroidJUnit4::class)
class GetBotInformationToolTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var json: Json

    private lateinit var target: GetBotInformationTool

    @Before
    fun setUp() {
        target = GetBotInformationTool(json)
    }

    @Test
    fun `Get user decision`() = runTest {
        assertEquals(
            UserDecision.None,
            target.getUserDecision()
        )
    }

    @Test
    fun `Success handle tool call`() = runTest {
        whenever(json.encodeToString<GetBotInformationTool.Result>(any(), any()))
            .doReturn("{/*success_json*/}")

        assertEquals(
            "{/*success_json*/}",
            target.handle(UserDecisionResult.None, "test_call_id", "{}")
        )
    }

    @Test
    fun `Failed handle tool call by exception`() = runTest {
        whenever(json.encodeToString<GetBotInformationTool.Result>(any(), any()))
            .doThrow(SerializationException())
        whenever(
            json.encodeToString(
                any(),
                eq(mapOf("reason" to "SerializationException"))
            )
        ).doReturn("{/*failed_json*/}")

        assertEquals(
            "{/*failed_json*/}",
            target.handle(UserDecisionResult.None, "test_call_id", "{}")
        )
    }
}
