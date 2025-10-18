package jp.co.nintendo.chat.data.repository.impl.message.assistant.repository

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import jp.co.nintendo.chat.data.repository.impl.message.assistant.factory.AiAssistantChatRequestFactory
import jp.co.nintendo.chat.data.repository.impl.message.factory.ChatMessageEntityFactory
import jp.co.nintendo.chat.data.repository.impl.message.mapper.ChatMessageMapper
import jp.co.nintendo.chat.data.source.local.message.ChatMessageLocalDataSource
import jp.co.nintendo.chat.data.source.local.message.entity.ChatMessageEntity
import jp.co.nintendo.chat.data.source.local.message.model.ChatMessageInsertResult
import jp.co.nintendo.chat.data.source.remote.assistant.AiAssistantChatRemoteDataSource
import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageRequest
import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageResponse
import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageResponse.InProgress.ChoiceAssembleSnapshot
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ChoiceDto
import jp.co.nintendo.chat.model.message.ChatMessage
import jp.co.nintendo.chat.model.message.ChatMessageRequest
import jp.co.nintendo.chat.model.message.content.MessageContent
import jp.co.nintendo.chat.model.message.extras.AiAssistantExtras
import jp.co.nintendo.chat.model.message.extras.MessageSenderExtras
import jp.co.nintendo.chat.model.message.lifecycle.MessageExchangeLifecycle
import jp.co.nintendo.chat.model.message.paging.MessagePageAnchor
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import kotlin.test.assertEquals

/**
 * An unit test for [AiAssistantChatRepositoryImpl]
 */
@RunWith(AndroidJUnit4::class)
class AiAssistantChatRepositoryImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var messageLocalDataSource: ChatMessageLocalDataSource

    @Mock
    private lateinit var aiAssistantChatStreamDataSource: AiAssistantChatRemoteDataSource

    @Mock
    private lateinit var chatMessageMapper: ChatMessageMapper

    @Mock
    private lateinit var chatMessageEntityFactory: ChatMessageEntityFactory

    @Mock
    private lateinit var aiAssistantChatRequestFactory: AiAssistantChatRequestFactory

    private lateinit var target: AiAssistantChatRepositoryImpl

    @Before
    fun setUp() {
        target = AiAssistantChatRepositoryImpl(
            messageLocalDataSource,
            aiAssistantChatStreamDataSource,
            aiAssistantChatRequestFactory,
            chatMessageMapper,
            chatMessageEntityFactory
        )
    }

    @Test
    fun `Observe latest message`() = runTest {
        val mockMessageEntity = mock<ChatMessageEntity>()
        whenever(messageLocalDataSource.observeLatestMessage("channelId"))
            .doReturn(flowOf(mockMessageEntity))

        val mockMessage = mock<ChatMessage>()
        whenever(chatMessageMapper.mapToDomain(mockMessageEntity))
            .doReturn(mockMessage)
        val actual = target.observeLatestMessage("channelId").toList()
        assertEquals(1, actual.size)
        assertEquals(mockMessage, actual.first())
    }

    @Test
    fun `Select latest message`() = runTest {
        val mockMessageEntity = mock<ChatMessageEntity>()
        whenever(messageLocalDataSource.selectLatestMessage("channelId"))
            .doReturn(mockMessageEntity)

        val mockMessage = mock<ChatMessage>()
        whenever(chatMessageMapper.mapToDomain(mockMessageEntity))
            .doReturn(mockMessage)
        val actual = target.selectLatestMessage("channelId")
        assertEquals(mockMessage, actual)
    }

    @Test
    fun `Load latest message page`() = runTest {
        val mockMessageEntity = mock<ChatMessageEntity>()
        val pagingData = PagingData.from(listOf(mockMessageEntity))
        whenever(messageLocalDataSource.selectMessagePagingSource("channelId", 0))
            .doReturn(flowOf(pagingData))
        val mockMessage = mock<ChatMessage>()
        whenever(chatMessageMapper.mapToDomain(mockMessageEntity))
            .doReturn(mockMessage)
        val actualList = target.loadMessagePage(MessagePageAnchor.Latest("channelId")).asSnapshot()
        assertEquals(1, actualList.size)
        assertEquals(mockMessage, actualList.first())
    }

    @Test
    fun `Load message page with anchor`() = runTest {
        val mockMessageEntity = mock<ChatMessageEntity> {
            on { localMessageId } doReturn "MSG_100"
            on { createdAtMillis } doReturn 1000L
        }
        val pagingData = PagingData.from(listOf(mockMessageEntity))
        whenever(
            messageLocalDataSource.countNewerOrEqual(
                "channelId",
                anchorLocalMessageId = "MSG_100",
                anchorCreatedAt = 1000L
            )
        ).doReturn(100)
        whenever(messageLocalDataSource.selectMessage("MSG_100"))
            .doReturn(mockMessageEntity)
        whenever(
            messageLocalDataSource.selectMessagePagingSource(
                "channelId",
                100
            )
        ).doReturn(flowOf(pagingData))
        val mockMessage = mock<ChatMessage>()
        whenever(chatMessageMapper.mapToDomain(mockMessageEntity))
            .doReturn(mockMessage)
        val actualList = target.loadMessagePage(
            anchor = MessagePageAnchor.Around(channelId = "channelId", localMessageId = "MSG_100")
        ).asSnapshot()
        assertEquals(1, actualList.size)
        assertEquals(mockMessage, actualList.first())
    }

    @Test
    fun `Select message`() = runTest {
        val mockMessageEntity = mock<ChatMessageEntity>()
        whenever(messageLocalDataSource.selectMessage("MSG_01"))
            .doReturn(mockMessageEntity)

        val mockMessage = mock<ChatMessage>()
        whenever(chatMessageMapper.mapToDomain(mockMessageEntity))
            .doReturn(mockMessage)

        assertEquals(
            mockMessage,
            target.selectMessage("MSG_01")
        )
    }

    @Test
    fun `Exchange message failed during insert message`() = runTest {
        val mockContent = mock<MessageContent>()
        val mockSenderExtras = mock<MessageSenderExtras>()
        val mockSentMessageEntity = mock<ChatMessageEntity> {
            on { localMessageId } doReturn "MSG_01"
        }
        whenever(
            chatMessageEntityFactory.create(
                "channelId",
                mockContent,
                mockSenderExtras
            )
        ).doReturn(mockSentMessageEntity)

        whenever(messageLocalDataSource.insert(mockSentMessageEntity))
            .doReturn(ChatMessageInsertResult.Failure.Unknown)

        target.exchangeMessage(
            "channelId",
            ChatMessageRequest(mockContent, mockSenderExtras)
        ).test {
            assertEquals(
                MessageExchangeLifecycle.Sending(sendingLocalMessageId = "MSG_01"),
                awaitItem()
            )

            assertEquals(
                MessageExchangeLifecycle.Failure,
                awaitItem()
            )

            awaitComplete()
        }
    }

    @Test
    fun `Exchange message failed`() = runTest {
        val mockContent = mock<MessageContent>()
        val mockSenderExtras = mock<MessageSenderExtras>()
        val mockSentMessageEntity = mock<ChatMessageEntity> {
            on { localMessageId } doReturn "MSG_01"
        }
        whenever(
            chatMessageEntityFactory.create(
                "channelId",
                mockContent,
                mockSenderExtras
            )
        ).doReturn(mockSentMessageEntity)

        whenever(messageLocalDataSource.insert(mockSentMessageEntity))
            .doReturn(ChatMessageInsertResult.Success)

        val mockLatestMessageEntity = mock<ChatMessageEntity>()
        whenever(
            messageLocalDataSource.selectLatestMessages(
                channelId = "channelId",
                limit = 50
            )
        ).doReturn(listOf(mockLatestMessageEntity))

        val mockLatestMessage = mock<ChatMessage>()
        whenever(chatMessageMapper.mapToDomain(mockLatestMessageEntity))
            .doReturn(mockLatestMessage)


        val mockRequest = mock<AiAssistantExchangeMessageRequest>()
        whenever(
            aiAssistantChatRequestFactory.create(
                listOf(mockLatestMessage)
            )
        ).doReturn(mockRequest)

        val mockProgressResponse = AiAssistantExchangeMessageResponse.InProgress(
            responseId = "response_01",
            choices = listOf(
                ChoiceAssembleSnapshot.ToolCall
            )
        )
        whenever(aiAssistantChatStreamDataSource.exchangeMessage(mockRequest))
            .doReturn(
                flowOf(
                    mockProgressResponse,
                    AiAssistantExchangeMessageResponse.Failure.Unknown
                )
            )

        target.exchangeMessage(
            "channelId",
            ChatMessageRequest(mockContent, mockSenderExtras)
        ).test {
            assertEquals(
                MessageExchangeLifecycle.Sending(sendingLocalMessageId = "MSG_01"),
                awaitItem()
            )

            assertEquals(
                MessageExchangeLifecycle.StreamingResponseToolRequest,
                awaitItem()
            )

            assertEquals(
                MessageExchangeLifecycle.Failure,
                awaitItem()
            )

            awaitComplete()
        }
    }

    @Test
    fun `Exchange message success`() = runTest {
        val mockContent = mock<MessageContent>()
        val mockSenderExtras = mock<MessageSenderExtras>()
        val mockSentMessageEntity = mock<ChatMessageEntity> {
            on { localMessageId } doReturn "MSG_01"
        }
        whenever(
            chatMessageEntityFactory.create(
                "channelId",
                mockContent,
                mockSenderExtras
            )
        ).doReturn(mockSentMessageEntity)

        whenever(messageLocalDataSource.insert(mockSentMessageEntity))
            .doReturn(ChatMessageInsertResult.Success)

        val mockLatestMessageEntity = mock<ChatMessageEntity>()
        whenever(
            messageLocalDataSource.selectLatestMessages(
                channelId = "channelId",
                limit = 50
            )
        ).doReturn(listOf(mockLatestMessageEntity))

        val mockLatestMessage = mock<ChatMessage>()
        whenever(chatMessageMapper.mapToDomain(mockLatestMessageEntity))
            .doReturn(mockLatestMessage)


        val mockRequest = mock<AiAssistantExchangeMessageRequest>()
        whenever(
            aiAssistantChatRequestFactory.create(
                listOf(mockLatestMessage)
            )
        ).doReturn(mockRequest)

        val progressResponse1 = AiAssistantExchangeMessageResponse.InProgress(
            responseId = "response_01",
            choices = listOf(
                ChoiceAssembleSnapshot.Content("He")
            )
        )

        val progressResponse2 = AiAssistantExchangeMessageResponse.InProgress(
            responseId = "response_01",
            choices = listOf(
                ChoiceAssembleSnapshot.Content("Hell")
            )
        )

        val doneFirstChoiceDto = ChoiceDto(
            role = "ai_assistant",
            content = "Hello",
            toolCalls = emptyList()
        )
        val doneResponse = AiAssistantExchangeMessageResponse.Done(
            responseId = "response_01",
            choices = listOf(doneFirstChoiceDto)
        )
        whenever(aiAssistantChatStreamDataSource.exchangeMessage(mockRequest))
            .doReturn(
                flowOf(
                    progressResponse1,
                    progressResponse2,
                    doneResponse
                )
            )

        val mockResponseMessageEntity = mock<ChatMessageEntity>()
        whenever(
            chatMessageEntityFactory.createAiAssistantResponseMessage(
                "channelId",
                "response_01",
                doneFirstChoiceDto
            )
        ).doReturn(mockResponseMessageEntity)
        whenever(messageLocalDataSource.insert(mockResponseMessageEntity))
            .doReturn(ChatMessageInsertResult.Success)

        target.exchangeMessage(
            "channelId",
            ChatMessageRequest(mockContent, mockSenderExtras)
        ).test {
            assertEquals(
                MessageExchangeLifecycle.Sending(sendingLocalMessageId = "MSG_01"),
                awaitItem()
            )

            assertEquals(
                MessageExchangeLifecycle.StreamingResponseContent(
                    content = "He",
                    senderExtras = AiAssistantExtras(responseId = "response_01")
                ),
                awaitItem()
            )

            assertEquals(
                MessageExchangeLifecycle.StreamingResponseContent(
                    content = "Hell",
                    senderExtras = AiAssistantExtras(responseId = "response_01")
                ),
                awaitItem()
            )

            assertEquals(
                MessageExchangeLifecycle.Done,
                awaitItem()
            )

            awaitComplete()
        }
    }

    @Test
    fun `Delete message`() = runTest {
        target.deleteMessage("MSG_01")
        verify(messageLocalDataSource).deleteMessage("MSG_01")
    }
}
