package jp.co.nintendo.chat.data.repository.channel

import androidx.paging.PagingData
import jp.co.nintendo.chat.model.channel.ChatChannel
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