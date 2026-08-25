package dev.headwind.chat.data.repository.channel

import androidx.paging.PagingData
import dev.headwind.chat.model.channel.ChatChannel
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