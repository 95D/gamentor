package jp.co.nintendo.chat.data.source.local.impl.message.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    indices = [
        Index(value = ["channelId"]),
        Index(value = ["channelId", "createdAtMillis"]),
        Index(value = ["channelId", "createdAtMillis", "localMessageId"], unique = true)
    ]
)
data class ChatMessageDbEntity(
    @PrimaryKey
    val localMessageId: String,
    val channelId: String,
    val createdAtMillis: Long,
    val contentJson: String,
    val senderExtrasJson: String
)
