package jp.co.nintendo.chat.data.source.local.impl.message

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteFullException
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import jp.co.nintendo.chat.data.source.local.impl.message.dao.ChatMessageDao
import jp.co.nintendo.chat.data.source.local.impl.message.entity.ChatMessageDbEntity
import jp.co.nintendo.chat.data.source.local.message.ChatMessageLocalDataSource
import jp.co.nintendo.chat.data.source.local.message.entity.ChatMessageEntity
import jp.co.nintendo.chat.data.source.local.message.model.ChatMessageInsertResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class ChatMessageLocalDataSourceImpl @Inject constructor(
    val messageDao: ChatMessageDao
) : ChatMessageLocalDataSource {
    override fun observeLatestMessage(channelId: String): Flow<ChatMessageEntity?> =
        messageDao.observeLatestMessage(channelId).map { it?.let(this::mapToEntity) }

    override suspend fun selectLatestMessage(channelId: String): ChatMessageEntity? {
        try {
            val dbentity = messageDao.observeLatestMessage(channelId).first()
            Timber.d(dbentity.toString())
            return dbentity?.let(this::mapToEntity)
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }
    }

    override suspend fun selectLatestMessages(
        channelId: String,
        limit: Int
    ): List<ChatMessageEntity> = messageDao.selectLatestMessages(channelId, limit)
        .map(this::mapToEntity)

    override fun selectMessagePagingSource(
        channelId: String,
        initialKey: Int
    ): Flow<PagingData<ChatMessageEntity>> = Pager(
        config = createMessagePagingConfig(),
        initialKey = initialKey,
        pagingSourceFactory = { messageDao.selectMessagePagingSource(channelId) }
    ).flow.map { it.map(this::mapToEntity) }

    private fun createMessagePagingConfig(): PagingConfig = PagingConfig(
        pageSize = PAGE_SIZE,
        initialLoadSize = PAGE_SIZE * 2,
        enablePlaceholders = false
    )

    override suspend fun selectMessage(localMessageId: String): ChatMessageEntity? =
        messageDao.selectMessage(localMessageId)?.let(this::mapToEntity)

    override suspend fun countNewerOrEqual(
        channelId: String,
        anchorLocalMessageId: String,
        anchorCreatedAt: Long
    ): Int = messageDao.countNewerOrEqual(channelId, anchorLocalMessageId, anchorCreatedAt)

    override fun insert(entity: ChatMessageEntity): ChatMessageInsertResult =
        try {
            messageDao.insert(mapToDbEntity(entity))
            ChatMessageInsertResult.Success
        } catch (e: SQLiteConstraintException) {
            Timber.e(e)
            ChatMessageInsertResult.Failure.Unknown
        } catch (e: SQLiteFullException) {
            Timber.e(e)
            ChatMessageInsertResult.Failure.FullDisk
        }

    override fun deleteMessage(localMessageId: String) =
        messageDao.deleteMessage(localMessageId)

    override fun deleteAllMessagesInChannel(channelId: String) =
        messageDao.deleteAllMessagesInChannel(channelId)

    private fun mapToEntity(dbEntity: ChatMessageDbEntity): ChatMessageEntity = ChatMessageEntity(
        localMessageId = dbEntity.localMessageId,
        channelId = dbEntity.channelId,
        createdAtMillis = dbEntity.createdAtMillis,
        contentJson = dbEntity.contentJson,
        senderExtrasJson = dbEntity.senderExtrasJson
    )

    private fun mapToDbEntity(entity: ChatMessageEntity): ChatMessageDbEntity = ChatMessageDbEntity(
        localMessageId = entity.localMessageId,
        channelId = entity.channelId,
        createdAtMillis = entity.createdAtMillis,
        contentJson = entity.contentJson,
        senderExtrasJson = entity.senderExtrasJson
    )

    private companion object {
        const val PAGE_SIZE = 3// 50
    }
}
