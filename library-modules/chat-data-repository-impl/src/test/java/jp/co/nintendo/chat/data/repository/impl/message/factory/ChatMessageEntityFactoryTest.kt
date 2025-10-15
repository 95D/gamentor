package jp.co.nintendo.chat.data.repository.impl.message.factory

import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.nintendo.chat.data.repository.impl.time.SystemCurrentMillisCalculator
import jp.co.nintendo.chat.data.source.local.message.entity.ChatMessageEntity
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ChoiceDto
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ToolCallDto
import jp.co.nintendo.chat.domain.message.model.content.MessageContent
import jp.co.nintendo.chat.domain.message.model.content.TextContent
import jp.co.nintendo.chat.domain.message.model.content.ToolProcessContent
import jp.co.nintendo.chat.domain.message.model.extras.AiAssistantExtras
import jp.co.nintendo.chat.domain.message.model.extras.MessageSenderExtras
import jp.co.nintendo.id.domain.factory.EntityIdFactory
import jp.co.nintendo.id.domain.model.DomainCode
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
 * An unit test for [ChatMessageEntityFactory]
 */
@RunWith(AndroidJUnit4::class)
class ChatMessageEntityFactoryTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var json: Json

    @Mock
    private lateinit var entityIdFactory: EntityIdFactory

    @Mock
    private lateinit var systemCurrentMillisCalculator: SystemCurrentMillisCalculator

    private lateinit var target: ChatMessageEntityFactory

    @Before
    fun setUp() {
        target = ChatMessageEntityFactory(
            json,
            systemCurrentMillisCalculator,
            entityIdFactory
        )
    }

    @Test
    fun `Create message`() {
        val mockContent = mock<MessageContent>()
        val mockSenderExtras = mock<MessageSenderExtras>()
        whenever(
            json.encodeToString(
                any(),
                eq(mockContent)
            )
        ).doReturn("{/*content*/}")
        whenever(
            json.encodeToString(
                any(),
                eq(mockSenderExtras)
            )
        ).doReturn("{/*senderExtras*/}")
        whenever(entityIdFactory.create(DomainCode.ChatMessage))
            .doReturn("MSG_01")
        whenever(systemCurrentMillisCalculator.getCurrentMillis())
            .doReturn(1000L)
        assertEquals(
            ChatMessageEntity(
                localMessageId = "MSG_01",
                channelId = "test_channel",
                createdAtMillis = 1000L,
                contentJson = "{/*content*/}",
                senderExtrasJson = "{/*senderExtras*/}"
            ),
            target.create(
                channelId = "test_channel",
                content = mockContent,
                senderExtras = mockSenderExtras
            )
        )
    }

    @Test
    fun `Create ai assistant text message`() {
        whenever(
            json.encodeToString(
                any(),
                eq(TextContent(rawText = "Hello"))
            )
        ).doReturn("{/*content*/}")
        whenever(
            json.encodeToString(
                any(),
                eq(AiAssistantExtras(responseId = "response_01"))
            )
        ).doReturn("{/*senderExtras*/}")
        whenever(entityIdFactory.create(DomainCode.ChatMessage))
            .doReturn("MSG_01")
        whenever(systemCurrentMillisCalculator.getCurrentMillis())
            .doReturn(1000L)
        assertEquals(
            ChatMessageEntity(
                localMessageId = "MSG_01",
                channelId = "test_channel",
                createdAtMillis = 1000L,
                contentJson = "{/*content*/}",
                senderExtrasJson = "{/*senderExtras*/}"
            ),
            target.createAiAssistantResponseMessage(
                channelId = "test_channel",
                responseId = "response_01",
                choice = ChoiceDto(
                    role = "assistatnt",
                    content = "Hello",
                    toolCalls = emptyList()
                ),
            )
        )
    }

    @Test
    fun `Create ai assistant tool call message`() {
        whenever(
            json.encodeToString(
                any(),
                eq(
                    ToolProcessContent(
                        toolCalls = listOf(
                            ToolProcessContent.ToolCall(
                                toolCallId = "test_call_01",
                                toolName = "foo",
                                argumentsJson = "{}"
                            )
                        ),
                        toolReturns = emptyList()
                    )
                )
            )
        ).doReturn("{/*content*/}")
        whenever(
            json.encodeToString(
                any(),
                eq(AiAssistantExtras(responseId = "response_01"))
            )
        )
            .doReturn("{/*senderExtras*/}")
        whenever(entityIdFactory.create(DomainCode.ChatMessage))
            .doReturn("MSG_01")
        whenever(systemCurrentMillisCalculator.getCurrentMillis())
            .doReturn(1000L)
        assertEquals(
            ChatMessageEntity(
                localMessageId = "MSG_01",
                channelId = "test_channel",
                createdAtMillis = 1000L,
                contentJson = "{/*content*/}",
                senderExtrasJson = "{/*senderExtras*/}"
            ),
            target.createAiAssistantResponseMessage(
                channelId = "test_channel",
                responseId = "response_01",
                choice = ChoiceDto(
                    role = "assistatnt",
                    content = "",
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
                )
            )
        )
    }
}
