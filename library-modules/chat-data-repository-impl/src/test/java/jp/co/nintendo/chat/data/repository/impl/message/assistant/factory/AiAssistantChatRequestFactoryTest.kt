package jp.co.nintendo.chat.data.repository.impl.message.assistant.factory

import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.nintendo.automation.domain.tool.usecase.GetToolSignaturesUseCase
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.MessageLogDto
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ToolCallDto
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ToolCatalogDto
import jp.co.nintendo.chat.model.message.ChatMessage
import jp.co.nintendo.chat.model.message.content.TextContent
import jp.co.nintendo.chat.model.message.content.ToolProcessContent
import jp.co.nintendo.chat.model.message.extras.AiAssistantExtras
import jp.co.nintendo.chat.model.message.extras.AppOwnerExtras
import kotlinx.serialization.json.JsonElement
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * An unit test for [AiAssistantChatRequestFactory]
 */
@RunWith(AndroidJUnit4::class)
class AiAssistantChatRequestFactoryTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var getToolSignaturesUseCase: GetToolSignaturesUseCase

    @Mock
    private lateinit var fooToolSignatureJson: JsonElement

    @Mock
    private lateinit var bobToolSignatureJson: JsonElement

    private lateinit var target: AiAssistantChatRequestFactory

    @Before
    fun setUp() {
        whenever(getToolSignaturesUseCase.getAllToolSignaturesJson()).doReturn(
            listOf(
                fooToolSignatureJson,
                bobToolSignatureJson
            )
        )
        target = AiAssistantChatRequestFactory(getToolSignaturesUseCase)
    }

    @Test
    fun `Create request`() {
        val mockMessage1 = mock<ChatMessage> {
            on { senderExtras } doReturn AppOwnerExtras
            on { content } doReturn TextContent(rawText = "Hello bot!")
        }

        val mockMessage2 = mock<ChatMessage> {
            on { senderExtras } doReturn AiAssistantExtras(responseId = "response_01")
            on { content } doReturn ToolProcessContent(
                toolCalls = listOf(
                    ToolProcessContent.ToolCall(
                        toolCallId = "test_call_01",
                        toolName = "foo",
                        argumentsJson = "{}"
                    )
                ),
                toolReturns = listOf(
                    ToolProcessContent.ToolReturn(
                        toolCallId = "test_call_01",
                        content = "{\"result\":true}"
                    )
                )
            )
        }

        val actual = target.create(listOf(mockMessage1, mockMessage2))

        // Tool assertion

        assertEquals(
            ToolCatalogDto(
                type = "function",
                function = fooToolSignatureJson
            ),
            actual.toolCatalogs[0]
        )

        assertEquals(
            ToolCatalogDto(
                type = "function",
                function = bobToolSignatureJson
            ),
            actual.toolCatalogs[1]
        )

        // Message assertion

        assertEquals(
            MessageLogDto(
                completionId = null,
                role = "user",
                content = "Hello bot!"
            ),
            actual.messages[0]
        )

        assertEquals(
            MessageLogDto(
                completionId = "response_01",
                role = "assistant",
                toolCalls = listOf(
                    ToolCallDto(
                        "test_call_01",
                        function = ToolCallDto.FunctionCall(
                            name = "foo",
                            arguments = "{}"
                        ),
                        type = "function"
                    )
                )
            ),
            actual.messages[1]
        )
    }
}
