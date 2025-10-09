package jp.co.nintendo.chat.data.source.remote.assistant

import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageRequest
import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageResponse
import kotlinx.coroutines.flow.Flow

/**
 * A remote data source for accessing AI assistant chat service
 */
interface AiAssistantChatRemoteDataSource {
    fun exchangeMessage(
        request: AiAssistantExchangeMessageRequest
    ): Flow<AiAssistantExchangeMessageResponse>
}
