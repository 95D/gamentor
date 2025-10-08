package jp.co.nintendo.automation.domain.impl.tool

import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.nintendo.automation.domain.impl.tool.usecase.GetToolSignaturesUseCaseImpl
import jp.co.nintendo.automation.domain.tool.model.ToolParameterSignature
import jp.co.nintendo.automation.domain.tool.model.ToolSignature
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * An unit test for [GetToolSignaturesUseCaseImpl]
 */
@RunWith(AndroidJUnit4::class)
class GetToolSignaturesUseCaseImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var toolFoo: Tool

    @Mock
    private lateinit var toolBob: Tool

    @Mock
    private lateinit var json: Json

    private lateinit var target: GetToolSignaturesUseCaseImpl

    @Before
    fun setUp() {
        target = GetToolSignaturesUseCaseImpl(
            mapOf("foo" to toolFoo, "bob" to toolBob),
            json
        )
    }

    @Test
    fun `Get tool signatures`() {
        val fooToolSignature = ToolSignature(
            toolName = "foo",
            toolDescription = "tool foo",
            parameters = ToolParameterSignature.EMPTY
        )
        whenever(toolFoo.toolSignature).doReturn(fooToolSignature)

        val bobToolSignature = ToolSignature(
            toolName = "bob",
            toolDescription = "tool bob",
            parameters = ToolParameterSignature.EMPTY
        )
        whenever(toolBob.toolSignature).doReturn(bobToolSignature)

        assertEquals(
            target.getAllToolSignatures(),
            listOf(
                fooToolSignature,
                bobToolSignature
            )
        )
    }

    @Test
    fun `Get tool signatures as json elements`() {
        val fooToolSignature = ToolSignature(
            toolName = "foo",
            toolDescription = "tool foo",
            parameters = ToolParameterSignature.EMPTY
        )
        whenever(toolFoo.toolSignature).doReturn(fooToolSignature)
        val mockFooSignatureJson = mock<JsonElement>()
        whenever(json.encodeToJsonElement(fooToolSignature))
            .doReturn(mockFooSignatureJson)

        val bobToolSignature = ToolSignature(
            toolName = "bob",
            toolDescription = "tool bob",
            parameters = ToolParameterSignature.EMPTY
        )
        whenever(toolBob.toolSignature).doReturn(bobToolSignature)
        val mockBobSignatureJson = mock<JsonElement>()
        whenever(json.encodeToJsonElement(bobToolSignature))
            .doReturn(mockBobSignatureJson)

        assertEquals(
            target.getAllToolSignaturesJson(),
            listOf(
                mockFooSignatureJson,
                mockBobSignatureJson
            )
        )
    }

    @Test
    fun `Get tool signature as json elements with filtering exception`() {
        val fooToolSignature = ToolSignature(
            toolName = "foo",
            toolDescription = "tool foo",
            parameters = ToolParameterSignature.EMPTY
        )
        whenever(toolFoo.toolSignature).doReturn(fooToolSignature)
        whenever(json.encodeToJsonElement(fooToolSignature))
            .doThrow(SerializationException())

        val bobToolSignature = ToolSignature(
            toolName = "bob",
            toolDescription = "tool bob",
            parameters = ToolParameterSignature.EMPTY
        )
        whenever(toolBob.toolSignature).doReturn(bobToolSignature)
        val mockBobSignatureJson = mock<JsonElement>()
        whenever(json.encodeToJsonElement(bobToolSignature))
            .doReturn(mockBobSignatureJson)

        assertEquals(
            target.getAllToolSignaturesJson(),
            listOf(
                mockBobSignatureJson
            )
        )
    }
}
