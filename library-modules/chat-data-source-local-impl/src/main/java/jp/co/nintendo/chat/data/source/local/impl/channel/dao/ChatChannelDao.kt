package jp.co.nintendo.chat.data.source.local.impl.channel.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jp.co.nintendo.chat.data.source.local.impl.channel.entity.ChatChannelDbEntity

/**
 * A data access object for accessing chat channel table
 */
@Dao
interface ChatChannelDao {
    @Query(
        """
        SELECT * FROM chat_channels
        ORDER BY channelId DESC 
    """
    )
    fun selectChannelPagingSource(): PagingSource<Int, ChatChannelDbEntity>

    @Query(
        """
        SELECT * FROM chat_channels
        WHERE channelId = :channelId
    """
    )
    fun selectChannel(channelId: String): ChatChannelDbEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insert(entity: ChatChannelDbEntity)

    @Query("DELETE FROM chat_channels WHERE channelId = :channelId")
    fun deleteChannel(channelId: String)
}
