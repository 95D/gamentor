package jp.co.nintendo.chat.data.source.local.impl.channel

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteFullException
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import jp.co.nintendo.chat.data.source.local.channel.ChatChannelLocalDataSource
import jp.co.nintendo.chat.data.source.local.channel.entity.ChatChannelEntity
import jp.co.nintendo.chat.data.source.local.channel.model.ChatChannelInsertResult
import jp.co.nintendo.chat.data.source.local.impl.channel.dao.ChatChannelDao
import jp.co.nintendo.chat.data.source.local.impl.channel.entity.ChatChannelDbEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * An implementation pf [ChatChannelLocalDataSource]
 */
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

    override fun insert(entity: ChatChannelEntity): ChatChannelInsertResult =
        try {
            chatChannelDao.insert(mapToDbEntity(entity))
            ChatChannelInsertResult.Success
        } catch (e: SQLiteConstraintException) {
            Timber.e(e)
            ChatChannelInsertResult.Failure.Unknown
        } catch (e: SQLiteFullException) {
            Timber.e(e)
            ChatChannelInsertResult.Failure.FullDisk
        }

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
