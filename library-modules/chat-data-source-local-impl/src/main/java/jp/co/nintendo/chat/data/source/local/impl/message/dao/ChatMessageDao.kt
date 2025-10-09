package jp.co.nintendo.chat.data.source.local.impl.message.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jp.co.nintendo.chat.data.source.local.impl.message.entity.ChatMessageDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query(
        """
        SELECT * FROM chat_messages 
        WHERE channelId = :channelId 
        ORDER BY createdAtMillis DESC, localMessageId DESC 
        LIMIT 1
    """
    )
    fun observeLatestMessage(channelId: String): Flow<ChatMessageDbEntity?>
    @Query(
        """
        SELECT * FROM (
            SELECT * FROM chat_messages 
            WHERE channelId = :channelId 
            ORDER BY createdAtMillis DESC, localMessageId DESC
            LIMIT :limit
        )  AS latest_messages 
        ORDER BY createdAtMillis ASC;
    """
    )
    suspend fun selectLatestMessages(channelId: String, limit: Int): List<ChatMessageDbEntity>

    @Query(
        """
        SELECT * FROM chat_messages 
        WHERE channelId = :channelId 
        ORDER BY createdAtMillis DESC, localMessageId DESC 
    """
    )
    fun selectMessagePagingSource(channelId: String): PagingSource<Int, ChatMessageDbEntity>

    @Query(
        """
        SELECT * FROM chat_messages
        WHERE localMessageId = :localMessageId
    """
    )
    suspend fun selectMessage(localMessageId: String): ChatMessageDbEntity?

    @Query(
        """
        SELECT COUNT(*) FROM chat_messages
        WHERE channelId = :channelId AND (
            createdAtMillis > :anchorCreatedAt
            OR (createdAtMillis = :anchorCreatedAt AND localMessageId > :anchorLocalMessageId)
        )
    """
    )
    suspend fun countNewerOrEqual(
        channelId: String,
        anchorLocalMessageId: String,
        anchorCreatedAt: Long
    ): Int

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insert(entity: ChatMessageDbEntity)

    @Query("DELETE FROM chat_messages WHERE localMessageId = :localMessageId")
    fun deleteMessage(localMessageId: String)

    @Query("DELETE FROM chat_messages WHERE channelId = :channelId")
    fun deleteAllMessagesInChannel(channelId: String)
}
