package jp.co.nintendo.chat.domain.message.repository

import androidx.paging.PagingData
import jp.co.nintendo.chat.domain.message.model.ChatMessage
import jp.co.nintendo.chat.domain.message.model.ChatMessageRequest
import jp.co.nintendo.chat.domain.message.model.lifecycle.MessageExchangeLifecycle
import jp.co.nintendo.chat.domain.message.model.paging.MessagePageAnchor
import kotlinx.coroutines.flow.Flow

/**
 * A repository for managing chat messages within channels
 */
interface ChatMessageRepository {
    fun observeLatestMessage(channelId: String): Flow<ChatMessage?>
    suspend fun loadMessagePage(
        channelId: String,
        anchor: MessagePageAnchor
    ): Flow<PagingData<ChatMessage>>
    suspend fun selectMessage(localMessageId: String): ChatMessage?
    suspend fun exchangeMessage(
        channelId: String,
        messageRequest: ChatMessageRequest
    ): Flow<MessageExchangeLifecycle>
}
