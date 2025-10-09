package jp.co.nintendo.chat.data.source.local.impl.channel

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import jp.co.nintendo.chat.data.source.local.channel.ChatChannelLocalDataSource
import jp.co.nintendo.chat.data.source.local.channel.entity.ChatChannelEntity
import jp.co.nintendo.chat.data.source.local.impl.channel.dao.ChatChannelDao
import jp.co.nintendo.chat.data.source.local.impl.channel.entity.ChatChannelDbEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatChannelLocalDataSourceImpl @Inject constructor(
    private val chatChannelDao: ChatChannelDao
) : ChatChannelLocalDataSource {
    override fun selectChannelPagingSource(initialKey: Int): Flow<PagingData<ChatChannelEntity>> =
        Pager(
            config = createMessagePagingConfig(),
            initialKey = initialKey,
            pagingSourceFactory = { chatChannelDao.selectChannelPagingSource() }
        ).flow.map { it.map(this::mapToEntity) }

    private fun createMessagePagingConfig(): PagingConfig = PagingConfig(
        pageSize = PAGE_SIZE,
        initialLoadSize = PAGE_SIZE * 2,
        enablePlaceholders = true
    )

    override fun selectChannel(channelId: String): ChatChannelEntity? =
        chatChannelDao.selectChannel(channelId)?.let(this::mapToEntity)

    override fun insert(entity: ChatChannelEntity) =
        chatChannelDao.insert(mapToDbEntity(entity))

    override fun deleteChannel(channelId: String) =
        chatChannelDao.deleteChannel(channelId)

    private fun mapToEntity(dbEntity: ChatChannelDbEntity): ChatChannelEntity = ChatChannelEntity(
        channelId = dbEntity.channelId,
        displayName = dbEntity.displayName
    )

    private fun mapToDbEntity(entity: ChatChannelEntity): ChatChannelDbEntity = ChatChannelDbEntity(
        channelId = entity.channelId,
        displayName = entity.displayName
    )

    private companion object {
        const val PAGE_SIZE = 50
    }
}
