package jp.co.nintendo.chat.data.source.remote.impl.assistant

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageRequest
import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageResponse
import jp.co.nintendo.chat.data.source.remote.impl.assistant.stream.ChunkAssembleTask
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import kotlin.test.assertEquals

/**
 * An unit test for [AiAssistantChatRemoteDataSourceImpl]
 */
@RunWith(AndroidJUnit4::class)
class AiAssistantChatRemoteDataSourceImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var client: OkHttpClient

    @Mock
    private lateinit var json: Json

    @Mock
    private lateinit var eventSourceFactory: EventSource.Factory

    @Mock
    private lateinit var eventSource: EventSource

    @Mock
    private lateinit var chunkAssembleTask: ChunkAssembleTask

    private lateinit var target: AiAssistantChatRemoteDataSourceImpl

    @Before
    fun setUp() {
        target = AiAssistantChatRemoteDataSourceImpl(
            client,
            "https://test.co.jp".toHttpUrl(),
            json,
            chunkAssembleTaskSupplier = { chunkAssembleTask }
        )
    }

    @Test
    fun `Exchange message success with progress`() = runTest {
        val mockRequest = mock<AiAssistantExchangeMessageRequest>()
        whenever(json.encodeToString(mockRequest))
            .doReturn("{/*requestJson*/}")
        val response1 = mock<AiAssistantExchangeMessageResponse.InProgress>()
        val response2 = mock<AiAssistantExchangeMessageResponse.InProgress>()
        val response3 = mock<AiAssistantExchangeMessageResponse.Done>()
        chunkAssembleTask.stub {
            on { processData("{/*test1*/}") } doReturn response1
            on { processData("{/*test2*/}") } doReturn response2
            on { processData("[DONE]") } doReturn response3
        }
        whenever(eventSourceFactory.newEventSource(any(), any()))
            .doReturn(eventSource)
        Mockito.mockStatic(EventSources::class.java).use {
            it.`when`<EventSource.Factory> {
                EventSources.createFactory(client)
            }.thenReturn(eventSourceFactory)
            target.exchangeMessage(mockRequest).test {
                val listenerCaptor = argumentCaptor<EventSourceListener>()
                verify(eventSourceFactory).newEventSource(any(), listenerCaptor.capture())
                val listener = listenerCaptor.firstValue
                listener.onEvent(eventSource, null, null, data = "{/*test1*/}")
                assertEquals(
                    response1,
                    awaitItem()
                )
                listener.onEvent(eventSource, null, null, data = "{/*test2*/}")
                assertEquals(
                    response2,
                    awaitItem()
                )
                listener.onEvent(eventSource, null, null, data = "[DONE]")
                assertEquals(
                    response3,
                    awaitItem()
                )
                awaitComplete()
            }
        }
    }

    @Test
    fun `Exchange message failed while streaming`() = runTest {
        val mockRequest = mock<AiAssistantExchangeMessageRequest>()
        whenever(json.encodeToString(mockRequest))
            .doReturn("{/*requestJson*/}")
        val response1 = mock<AiAssistantExchangeMessageResponse.InProgress>()
        val response2 = AiAssistantExchangeMessageResponse.Failure.Unknown
        chunkAssembleTask.stub {
            on { processData("{/*test1*/}") } doReturn response1
            on { processData("{/*test2*/}") } doReturn response2
        }
        whenever(eventSourceFactory.newEventSource(any(), any()))
            .doReturn(eventSource)
        Mockito.mockStatic(EventSources::class.java).use {
            it.`when`<EventSource.Factory> {
                EventSources.createFactory(client)
            }.thenReturn(eventSourceFactory)
            target.exchangeMessage(mockRequest).test {
                val listenerCaptor = argumentCaptor<EventSourceListener>()
                verify(eventSourceFactory).newEventSource(any(), listenerCaptor.capture())
                val listener = listenerCaptor.firstValue
                listener.onEvent(eventSource, null, null, data = "{/*test1*/}")
                assertEquals(
                    response1,
                    awaitItem()
                )
                listener.onEvent(eventSource, null, null, data = "{/*test2*/}")
                assertEquals(
                    response2,
                    awaitItem()
                )
                awaitComplete()
            }
        }
    }

    @Test
    fun `Exchange message failed while streaming by exception`() = runTest {
        val mockRequest = mock<AiAssistantExchangeMessageRequest>()
        whenever(json.encodeToString(mockRequest))
            .doReturn("{/*requestJson*/}")
        whenever(eventSourceFactory.newEventSource(any(), any()))
            .doThrow(RuntimeException())
        Mockito.mockStatic(EventSources::class.java).use {
            it.`when`<EventSource.Factory> {
                EventSources.createFactory(client)
            }.thenReturn(eventSourceFactory)
            target.exchangeMessage(mockRequest).test {
                assertEquals(
                    AiAssistantExchangeMessageResponse.Failure.Unknown,
                    awaitItem()
                )
                awaitComplete()
            }
        }
    }

    @Test
    fun `Exchange message flow is closed`() = runTest {
        val mockRequest = mock<AiAssistantExchangeMessageRequest>()
        whenever(json.encodeToString(mockRequest))
            .doReturn("{/*requestJson*/}")
        whenever(eventSourceFactory.newEventSource(any(), any()))
            .doReturn(eventSource)
        Mockito.mockStatic(EventSources::class.java).use {
            it.`when`<EventSource.Factory> {
                EventSources.createFactory(client)
            }.thenReturn(eventSourceFactory)
            target.exchangeMessage(mockRequest).test {
                cancel()
            }
            verify(eventSource).cancel()
        }
    }
}
