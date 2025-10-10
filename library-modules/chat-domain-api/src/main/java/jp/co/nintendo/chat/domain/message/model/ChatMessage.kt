package jp.co.nintendo.chat.domain.message.model

import jp.co.nintendo.chat.domain.message.model.content.MessageContent
import jp.co.nintendo.chat.domain.message.model.extras.MessageSenderExtras

/**
 * A model representing a message in a chat channel
 */
data class ChatMessage(
    val localMessageId: String,
    val createdAtMillis: Long,
    val content: MessageContent,
    val senderExtras: MessageSenderExtras
)
