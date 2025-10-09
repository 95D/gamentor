package jp.co.nintendo.chat.domain.message.model

import jp.co.nintendo.chat.domain.message.model.content.MessageContent
import jp.co.nintendo.chat.domain.message.model.extras.MessageSenderExtras

/**
 * Represents a message in a chat conversation between users and an AI assistant.
 * Provides type-safe handling of different message sources through sealed interface pattern.
 */
data class ChatMessage(
    val localMessageId: String,
    val createdAtMillis: Long,
    val content: MessageContent,
    val senderExtras: MessageSenderExtras
)
