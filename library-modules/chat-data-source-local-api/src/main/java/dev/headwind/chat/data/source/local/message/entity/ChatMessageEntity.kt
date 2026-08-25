package dev.headwind.chat.data.source.local.message.entity

/**
 * An entity model for chat message information recorded within the app
 */
data class ChatMessageEntity(
    val localMessageId: String,
    val channelId: String,
    val createdAtMillis: Long,
    val contentJson: String,
    val senderExtrasJson: String
)
