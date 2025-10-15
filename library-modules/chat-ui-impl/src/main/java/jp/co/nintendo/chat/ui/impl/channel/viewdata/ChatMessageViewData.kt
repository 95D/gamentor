package jp.co.nintendo.chat.ui.impl.channel.viewdata

import jp.co.nintendo.chat.domain.message.model.content.MessageContent

class ChatMessageViewData(
    val localMessageId: String,
    val createdDate: String,
    val bubbleType: MessageBubbleViewType,
    val visibleLevel: MessageVisibleLevel,
    val senderDisplayName: String,
    val content: MessageContent
)
