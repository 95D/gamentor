package dev.headwind.chat.model.message

import dev.headwind.chat.model.message.content.MessageContent
import dev.headwind.chat.model.message.extras.MessageSenderExtras

data class ChatMessageRequest(
    val messageContent: MessageContent,
    val senderExtras: MessageSenderExtras
)
