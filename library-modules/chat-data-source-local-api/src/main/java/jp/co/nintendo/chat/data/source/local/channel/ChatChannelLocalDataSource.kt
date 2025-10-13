package jp.co.nintendo.chat.data.source.local.channel

import androidx.paging.PagingData
import jp.co.nintendo.chat.data.source.local.channel.entity.ChatChannelEntity
import jp.co.nintendo.chat.data.source.local.channel.model.ChatChannelInsertResult
import kotlinx.coroutines.flow.Flow

/**
 * A local data source for accessing [ChatChannelEntity]
 */
interface ChatChannelLocalDataSource {
    fun selectChannelPagingSource(initialKey: Int): Flow<PagingData<ChatChannelEntity>>
    fun selectChannel(channelId: String): ChatChannelEntity?
    fun insert(entity: ChatChannelEntity): ChatChannelInsertResult
    fun deleteChannel(channelId: String)
}
