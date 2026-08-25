package dev.headwind.chat.ui.impl.channel.viewdata

import dev.headwind.chat.model.message.content.MessageContent

class ChatMessageProgressViewData(
    val bubbleType: MessageBubbleViewType,
    val visibleLevel: MessageVisibleLevel,
    val senderDisplayName: String,
    val content: MessageContent
)
