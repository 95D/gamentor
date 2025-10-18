package jp.co.nintendo.chat.ui.impl.channel.viewdata

import jp.co.nintendo.chat.model.message.content.MessageContent

class ChatMessageProgressViewData(
    val bubbleType: MessageBubbleViewType,
    val visibleLevel: MessageVisibleLevel,
    val senderDisplayName: String,
    val content: MessageContent
)
