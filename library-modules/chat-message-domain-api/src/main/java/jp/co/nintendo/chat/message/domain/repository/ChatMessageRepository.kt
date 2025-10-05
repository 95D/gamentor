package jp.co.nintendo.chat.message.domain.repository

import androidx.paging.PagingData
import jp.co.nintendo.chat.message.domain.model.ChatMessage
import jp.co.nintendo.chat.message.domain.model.paging.MessagePageAnchor
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing chat messages within channels
 */
interface ChatMessageRepository {
    suspend fun loadMessagePage(
        channelId: String,
        anchor: MessagePageAnchor
    ): Flow<PagingData<ChatMessage>>
    fun addMessage(message: ChatMessage)
}
