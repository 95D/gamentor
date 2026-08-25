package dev.headwind.chat.model.message

import dev.headwind.chat.model.message.content.MessageContent
import dev.headwind.chat.model.message.extras.MessageSenderExtras

/**
 * A model representing a message in a chat channel
 */
data class ChatMessage(
    val localMessageId: String,
    val createdAtMillis: Long,
    val content: MessageContent,
    val senderExtras: MessageSenderExtras
)
