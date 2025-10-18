package jp.co.nintendo.chat.model.message

import jp.co.nintendo.chat.model.message.content.MessageContent
import jp.co.nintendo.chat.model.message.extras.MessageSenderExtras

/**
 * A model representing a message in a chat channel
 */
data class ChatMessage(
    val localMessageId: String,
    val createdAtMillis: Long,
    val content: MessageContent,
    val senderExtras: MessageSenderExtras
)
