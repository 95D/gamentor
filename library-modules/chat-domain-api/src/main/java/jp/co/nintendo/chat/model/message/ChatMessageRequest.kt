package jp.co.nintendo.chat.model.message

import jp.co.nintendo.chat.model.message.content.MessageContent
import jp.co.nintendo.chat.model.message.extras.MessageSenderExtras

data class ChatMessageRequest(
    val messageContent: MessageContent,
    val senderExtras: MessageSenderExtras
)
