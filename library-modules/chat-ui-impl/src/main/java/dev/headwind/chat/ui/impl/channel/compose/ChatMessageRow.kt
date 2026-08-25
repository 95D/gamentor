package dev.headwind.chat.ui.impl.channel.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.headwind.chat.ui.impl.channel.compose.content.MessageContent
import dev.headwind.chat.ui.impl.channel.viewdata.ChatMessageViewData
import dev.headwind.chat.ui.impl.channel.viewdata.MessageBubbleViewType
import dev.headwind.design.system.theme.LocalAppSemanticColors

@Composable
fun ChatMessageRow(
    message: ChatMessageViewData,
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalAppSemanticColors.current
    val columnAlignment = when(message.bubbleType) {
        MessageBubbleViewType.MINE -> Alignment.End
        MessageBubbleViewType.OTHERS -> Alignment.Start
        MessageBubbleViewType.SYSTEM -> Alignment.Start
    }
    Column(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = columnAlignment
    ) {
        if (message.senderDisplayName.isNotBlank()) {
            Text(text = message.senderDisplayName, color = semanticColors.textDefault)
        }
        MessageBubble(
            type = message.bubbleType,
            visibleLevel = message.visibleLevel
        ) {
            Column(modifier = modifier.fillMaxWidth()) {
                MessageContent(message.bubbleType, message.visibleLevel, message.content)
                Spacer(Modifier.padding(vertical = 2.dp))
                Text(text = message.createdDate, color = semanticColors.messageBubbleTextNormal)
            }
        }
    }
}
