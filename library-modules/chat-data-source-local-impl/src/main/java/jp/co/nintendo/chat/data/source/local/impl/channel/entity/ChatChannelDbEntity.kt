package jp.co.nintendo.chat.data.source.local.impl.channel.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_channels",
    indices = [
    ]
)
data class ChatChannelDbEntity(
    @PrimaryKey
    val channelId: String,
    val displayName: String
)
