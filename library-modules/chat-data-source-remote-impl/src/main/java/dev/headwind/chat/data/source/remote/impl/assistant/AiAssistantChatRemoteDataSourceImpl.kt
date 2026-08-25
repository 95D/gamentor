package dev.headwind.chat.data.source.remote.impl.assistant

import dev.headwind.chat.data.source.remote.assistant.AiAssistantChatRemoteDataSource
import dev.headwind.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageRequest
import dev.headwind.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageResponse
import dev.headwind.chat.data.source.remote.impl.assistant.stream.ChunkAssembleTask
import dev.headwind.chat.data.source.remote.impl.di.ChatDataRemoteCommon
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import timber.log.Timber
import javax.inject.Inject

class AiAssistantChatRemoteDataSourceImpl @Inject constructor(
    @param:ChatDataRemoteCommon private val client: OkHttpClient,
    @param:ChatDataRemoteCommon private val baseUrl: HttpUrl,
    @param:ChatDataRemoteCommon private val json: Json,
    @param:ChatDataRemoteCommon private val chunkAssembleTaskSupplier: () -> ChunkAssembleTask
) : AiAssistantChatRemoteDataSource {
    override fun exchangeMessage(
        request: AiAssistantExchangeMessageRequest
    ): Flow<AiAssistantExchangeMessageResponse> = callbackFlow {
        val url = baseUrl.newBuilder()
            .addEncodedPathSegments(API_PATH)
            .build()
        Timber.d("Request messages: ${request.messages}")
        Timber.d("Request tools: ${request.toolCatalogs}")
        val requestJson = json.encodeToString(request)
        Timber.d("Request messages: $requestJson")
        val requestJsonBody = requestJson.toRequestBody(
            MEDIA_TYPE_JSON.toMediaType()
        )
        val request = Request.Builder()
            .url(url)
            .header(HEADER_NAME_ACCEPT, HEADER_VALUE_TEXT_EVENT_STREAM)
            .post(requestJsonBody)
            .build()

        val factory = EventSources.createFactory(client)
        val chunkAssembleTask = chunkAssembleTaskSupplier()
        val listener = object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                val response = chunkAssembleTask.processData(data)
                trySend(response)
                val isFailed = response is AiAssistantExchangeMessageResponse.Failure
                val isDone = data == DATA_DONE
                if (isDone || isFailed) {
                    close()
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                close(t ?: IllegalStateException("EventSource failure"))
            }
        }
        val eventSource = factory.newEventSource(request, listener)
        awaitClose {
            eventSource.cancel()
        }
    }.catch { emit(AiAssistantExchangeMessageResponse.Failure.Unknown) }

    private companion object {

        const val API_PATH = "v1/chats/assistant/ai"
        const val HEADER_NAME_ACCEPT = "Accept"
        const val HEADER_VALUE_TEXT_EVENT_STREAM = "text/event-stream"
        const val MEDIA_TYPE_JSON = "application/json"
        const val DATA_DONE = "[DONE]"
    }
}
