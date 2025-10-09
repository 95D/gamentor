package jp.co.nintendo.chat.data.source.remote.impl.assistant.stream

import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageResponse
import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageResponse.InProgress.ChoiceAssembleSnapshot
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import jp.co.nintendo.chat.data.source.remote.impl.assistant.model.AiAssistantChatResponseChunk
import jp.co.nintendo.chat.data.source.remote.impl.assistant.model.delta.ChoiceDelta
import jp.co.nintendo.chat.data.source.remote.impl.assistant.model.delta.ToolCallDelta
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertTrue
import org.mockito.kotlin.doThrow

/**
 * An unit test for [ChunkAssembleTask]
 */
@RunWith(AndroidJUnit4::class)
class ChunkAssembleTaskTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var json: Json
    private lateinit var target: ChunkAssembleTask

    @Before
    fun setUp() {
        target = ChunkAssembleTask(json)
    }

    @Test
    fun `Assemble single chunk`() {
        val testInput = "{\"mock\": \"input01\"}"
        val chunk = AiAssistantChatResponseChunk(
            "response_01", listOf(
                ChoiceDelta(
                    index = 0,
                    contentDelta = ChoiceDelta.ChoiceContentDelta(
                        role = "assistant",
                        content = "Hello World!"
                    ),
                    finishReason = "stop"
                )
            )
        )
        whenever(
            json.decodeFromString<AiAssistantChatResponseChunk>(testInput)
        ).doReturn(chunk)

        val inProgress = target.processData(testInput)
        val result = target.processData("[DONE]")

        val progress = inProgress as AiAssistantExchangeMessageResponse.InProgress
        assertEquals("response_01", progress.responseId)
        assertEquals(1, progress.choices.size)
        val firstChoice = progress.choices.first() as
                ChoiceAssembleSnapshot.Content
        assertEquals(
            "Hello World!",
            firstChoice.assembledContent
        )


        val done = result as AiAssistantExchangeMessageResponse.Done
        assertEquals("response_01", done.responseId)
        assertEquals(1, done.choices.size)
        assertEquals("Hello World!", done.choices.first().content)
        assertEquals("assistant", done.choices.first().role)
    }

    @Test
    fun `Assemble multiple chunk`() {
        val testInput1 = "{\"mock\": \"input01\"}"
        val testInput2 = "{\"mock\": \"input02\"}"
        val testInput3 = "{\"mock\": \"input03\"}"

        val chunk1 = AiAssistantChatResponseChunk(
            "response_01", listOf(
                ChoiceDelta(
                    index = 0,
                    contentDelta = ChoiceDelta.ChoiceContentDelta(content = "This is ")
                )
            )
        )
        val chunk2 = AiAssistantChatResponseChunk(
            "response_01", listOf(
                ChoiceDelta(
                    index = 0,
                    contentDelta = ChoiceDelta.ChoiceContentDelta(content = "the full ")
                )
            )
        )
        val chunk3 = AiAssistantChatResponseChunk(
            "response_01", listOf(
                ChoiceDelta(
                    index = 0,
                    contentDelta = ChoiceDelta.ChoiceContentDelta(content = "message.")
                )
            )
        )
        whenever(json.decodeFromString<AiAssistantChatResponseChunk>(testInput1)).doReturn(chunk1)
        whenever(json.decodeFromString<AiAssistantChatResponseChunk>(testInput2)).doReturn(chunk2)
        whenever(json.decodeFromString<AiAssistantChatResponseChunk>(testInput3)).doReturn(chunk3)

        val response1 = target.processData(testInput1)
        val response2 = target.processData(testInput2)
        val response3 = target.processData(testInput3)
        val result = target.processData("[DONE]")

        assertEquals(
            "This is ",
            (response1 as AiAssistantExchangeMessageResponse.InProgress).choices.first()
                .let { it as ChoiceAssembleSnapshot.Content }.assembledContent
        )

        assertEquals(
            "This is the full ",
            (response2 as AiAssistantExchangeMessageResponse.InProgress).choices.first()
                .let { it as ChoiceAssembleSnapshot.Content }.assembledContent
        )

        assertEquals(
            "This is the full message.",
            (response3 as AiAssistantExchangeMessageResponse.InProgress).choices.first()
                .let { it as ChoiceAssembleSnapshot.Content }
                .assembledContent
        )

        assertEquals(
            "This is the full message.",
            (result as AiAssistantExchangeMessageResponse.Done).choices.first().content
        )
    }

    @Test
    fun `Assemble chunk for single tool call`() {
        val testInput1 = "{\"mock\": \"input01\"}"
        val testInput2 = "{\"mock\": \"input02\"}"

        val chunk1 = AiAssistantChatResponseChunk(
            "response_01", listOf(
                ChoiceDelta(
                    index = 0, contentDelta = ChoiceDelta.ChoiceContentDelta(
                        toolCalls = listOf(
                            ToolCallDelta(
                                index = 0,
                                id = "tc_01",
                                type = "function",
                                function = ToolCallDelta.FunctionDelta(
                                    name = "get_weather",
                                    arguments = "{\"city\": \"Se"
                                )
                            )
                        )
                    )
                )
            )
        )
        val chunk2 = AiAssistantChatResponseChunk(
            "response_01", listOf(
                ChoiceDelta(
                    index = 0, contentDelta = ChoiceDelta.ChoiceContentDelta(
                        toolCalls = listOf(
                            ToolCallDelta(
                                index = 0,
                                function = ToolCallDelta.FunctionDelta(arguments = "oul\"}")
                            )
                        )
                    )
                )
            )
        )
        whenever(json.decodeFromString<AiAssistantChatResponseChunk>(testInput1))
            .doReturn(chunk1)
        whenever(json.decodeFromString<AiAssistantChatResponseChunk>(testInput2))
            .doReturn(chunk2)

        val response1 = target.processData(testInput1)
        target.processData(testInput2)
        val result = target.processData("[DONE]")

        assertTrue(
            (response1 as AiAssistantExchangeMessageResponse.InProgress).choices
                .first() is ChoiceAssembleSnapshot.ToolCall
        )

        val toolCall =
            (result as AiAssistantExchangeMessageResponse.Done).choices.first().toolCalls.first()
        assertEquals("tc_01", toolCall.id)
        assertEquals("get_weather", toolCall.function.name)
        assertEquals("{\"city\": \"Seoul\"}", toolCall.function.arguments)
    }

    @Test
    fun `Assemble chunk for multiple choices`() {
        val testInput1 = "{\"mock\": \"input01\"}"
        val testInput2 = "{\"mock\": \"input02\"}"

        val chunk1 = AiAssistantChatResponseChunk(
            "response_01", listOf(
                ChoiceDelta(
                    index = 2,
                    contentDelta = ChoiceDelta.ChoiceContentDelta(content = "C")
                ),
                ChoiceDelta(index = 0, contentDelta = ChoiceDelta.ChoiceContentDelta(content = "A"))
            )
        )
        val chunk2 = AiAssistantChatResponseChunk(
            "response_01", listOf(
                ChoiceDelta(index = 1, contentDelta = ChoiceDelta.ChoiceContentDelta(content = "B"))
            )
        )
        whenever(json.decodeFromString<AiAssistantChatResponseChunk>(testInput1))
            .doReturn(chunk1)
        whenever(json.decodeFromString<AiAssistantChatResponseChunk>(testInput2))
            .doReturn(chunk2)

        target.processData(testInput1)
        target.processData(testInput2)
        val result = target.processData("[DONE]")

        assertTrue(result is AiAssistantExchangeMessageResponse.Done)
        val choices = (result as AiAssistantExchangeMessageResponse.Done).choices
        assertEquals(3, choices.size)
        assertEquals("A", choices[0].content)
        assertEquals("B", choices[1].content)
        assertEquals("C", choices[2].content)
    }

    @Test
    fun `Assemble chunk failed by parsing error`() {
        val invalidJson = "{\"mock\": \"invalid\"}"
        whenever(json.decodeFromString<AiAssistantChatResponseChunk>(invalidJson))
            .doThrow(SerializationException("Parse Error"))

        val result = target.processData(invalidJson)

        assertTrue(result is AiAssistantExchangeMessageResponse.Failure.Unknown)
    }

    @Test
    fun `Assemble chunk failed by invalid completion id`() {
        val result = target.processData("[DONE]")

        assertTrue(result is AiAssistantExchangeMessageResponse.Failure.Unknown)
    }
}
