package jp.co.nintendo.chat.domain.message.model

import jp.co.nintendo.chat.domain.message.model.content.MessageContent
import jp.co.nintendo.chat.domain.message.model.extras.MessageSenderExtras

data class ChatMessageRequest(
    val messageContent: MessageContent,
    val senderExtras: MessageSenderExtras
)
