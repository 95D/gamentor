package dev.headwind.chat.data.source.local.impl.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.headwind.chat.data.source.local.impl.channel.dao.ChatChannelDao
import dev.headwind.chat.data.source.local.impl.channel.entity.ChatChannelDbEntity
import dev.headwind.chat.data.source.local.impl.message.dao.ChatMessageDao
import dev.headwind.chat.data.source.local.impl.message.entity.ChatMessageDbEntity

/**
 * A local database class of chat-data domain
 */
@Database(
    entities = [ChatMessageDbEntity::class, ChatChannelDbEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun getChatMessageDao(): ChatMessageDao
    abstract fun getChatChannelDao(): ChatChannelDao
}
