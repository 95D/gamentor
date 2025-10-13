package jp.co.nintendo.chat.domain.channel.repository

import androidx.paging.PagingData
import jp.co.nintendo.chat.domain.channel.model.ChatChannel
import kotlinx.coroutines.flow.Flow

/**
 * A repository for managing chat channels
 */
interface ChatChannelRepository {
    fun loadChannelPage(): Flow<PagingData<ChatChannel>>
    suspend fun createNewChatChannel(): String?
    suspend fun selectChannel(channelId: String): ChatChannel?
    suspend fun deleteChannel(channelId: String)
}