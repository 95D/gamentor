package jp.co.nintendo.chat.data.source.local.message

import androidx.paging.PagingData
import jp.co.nintendo.chat.data.source.local.message.entity.ChatMessageEntity
import jp.co.nintendo.chat.data.source.local.message.model.ChatMessageInsertResult
import kotlinx.coroutines.flow.Flow

/**
 *  A local data source for accessing [ChatMessageEntity]
 */
interface ChatMessageLocalDataSource {
    fun observeLatestMessage(channelId: String): Flow<ChatMessageEntity?>
    suspend fun selectLatestMessage(channelId: String): ChatMessageEntity?
    suspend fun selectLatestMessages(channelId: String, limit: Int): List<ChatMessageEntity>
    fun selectMessagePagingSource(
        channelId: String,
        initialKey: Int
    ): Flow<PagingData<ChatMessageEntity>>

    suspend fun selectMessage(localMessageId: String): ChatMessageEntity?
    suspend fun countNewerOrEqual(
        channelId: String,
        anchorLocalMessageId: String,
        anchorCreatedAt: Long
    ): Int

    fun insert(entity: ChatMessageEntity): ChatMessageInsertResult
    fun deleteMessage(localMessageId: String)
    fun deleteAllMessagesInChannel(channelId: String)
}
