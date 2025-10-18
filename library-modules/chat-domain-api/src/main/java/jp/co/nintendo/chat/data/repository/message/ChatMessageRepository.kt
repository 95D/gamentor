package jp.co.nintendo.chat.data.repository.message

import androidx.paging.PagingData
import jp.co.nintendo.chat.model.message.ChatMessage
import jp.co.nintendo.chat.model.message.ChatMessageRequest
import jp.co.nintendo.chat.model.message.content.MessageContent
import jp.co.nintendo.chat.model.message.lifecycle.MessageExchangeLifecycle
import jp.co.nintendo.chat.model.message.paging.MessagePageAnchor
import kotlinx.coroutines.flow.Flow

/**
 * A repository for managing chat messages within channels
 */
interface ChatMessageRepository {
    fun observeLatestMessage(channelId: String): Flow<ChatMessage?>
    suspend fun selectLatestMessage(channelId: String): ChatMessage?
    suspend fun loadMessagePage(anchor: MessagePageAnchor): Flow<PagingData<ChatMessage>>

    suspend fun selectMessage(localMessageId: String): ChatMessage?
    suspend fun updateMessageContent(
        channelId: String,
        localMessageId: String,
        messageContent: MessageContent
    ): Boolean

    suspend fun exchangeMessage(
        channelId: String,
        messageRequest: ChatMessageRequest
    ): Flow<MessageExchangeLifecycle>

    suspend fun exchangeCurrentMessages(
        channelId: String
    ): Flow<MessageExchangeLifecycle>

    suspend fun deleteMessage(localMessageId: String)
}