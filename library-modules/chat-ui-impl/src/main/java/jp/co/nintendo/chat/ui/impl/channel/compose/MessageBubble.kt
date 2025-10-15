package jp.co.nintendo.chat.ui.impl.channel.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.nintendo.chat.ui.impl.channel.viewdata.MessageBubbleViewType
import jp.co.nintendo.chat.ui.impl.channel.viewdata.MessageVisibleLevel
import jp.co.nintendo.design.system.colors.DarkGray
import jp.co.nintendo.design.system.colors.SemanticColors
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors

@Composable
fun MessageBubble(
    type: MessageBubbleViewType,
    visibleLevel: MessageVisibleLevel,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val semanticColors = LocalAppSemanticColors.current
    val bubbleRoundRadiusDp = 16.dp
    val bubbleTailRadiusDp = 0.dp
    val bubbleColor = getBubbleColor(type, visibleLevel, semanticColors)
    when (type) {
        MessageBubbleViewType.MINE -> MessageBubble(
            horizontalArrangement = Arrangement.End,
            bubbleColor = bubbleColor,
            bubbleOutlineColor = semanticColors.messageBubbleOutlineNone,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            bubbleShape = RoundedCornerShape(
                topStart = bubbleRoundRadiusDp,
                topEnd = bubbleTailRadiusDp,
                bottomStart = bubbleRoundRadiusDp,
                bottomEnd = bubbleRoundRadiusDp
            ),
            modifier = modifier
                .padding(start = 48.dp),
            content = content
        )

        MessageBubbleViewType.OTHERS -> MessageBubble(
            horizontalArrangement = Arrangement.Start,
            bubbleColor = bubbleColor,
            bubbleOutlineColor = semanticColors.messageBubbleOutlineOthers,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            bubbleShape = RoundedCornerShape(
                topStart = bubbleTailRadiusDp,
                topEnd = bubbleRoundRadiusDp,
                bottomStart = bubbleRoundRadiusDp,
                bottomEnd = bubbleRoundRadiusDp
            ),
            modifier = modifier
                .padding(end = 48.dp),
            content = content
        )

        MessageBubbleViewType.SYSTEM -> MessageBubble(
            horizontalArrangement = Arrangement.Center,
            bubbleColor = bubbleColor,
            bubbleOutlineColor = semanticColors.messageBubbleOutlineNone,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            bubbleShape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            modifier = modifier.padding(horizontal = 30.dp, vertical = 4.dp),
            content = content
        )
    }
}

private fun getBubbleColor(
    type: MessageBubbleViewType,
    visibleLevel: MessageVisibleLevel,
    semanticColors: SemanticColors
): Color {
    if (visibleLevel == MessageVisibleLevel.Developer) {
        return semanticColors.messageBubbleDeveloper
    }
    return when (type) {
        MessageBubbleViewType.MINE -> semanticColors.messageBubbleMine
        MessageBubbleViewType.OTHERS -> semanticColors.messageBubbleOthers
        MessageBubbleViewType.SYSTEM -> semanticColors.messageBubbleSystem
    }
}

@Composable
fun MessageBubble(
    bubbleColor: Color,
    bubbleOutlineColor: Color,
    contentPadding: PaddingValues,
    bubbleShape: RoundedCornerShape,
    horizontalArrangement: Arrangement.Horizontal,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(bubbleShape)
                .border(width = 1.dp, color = bubbleOutlineColor, shape = bubbleShape)
                .background(bubbleColor)
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageBubblePreview() {
    MaterialTheme {
        Surface {
            Column {
                MessageBubble(
                    type = MessageBubbleViewType.MINE,
                    visibleLevel = MessageVisibleLevel.User
                ) {
                    Text(text = "Message 1", color = DarkGray)
                }

                MessageBubble(
                    type = MessageBubbleViewType.MINE,
                    visibleLevel = MessageVisibleLevel.User
                ) {
                    Text(text = "Message 2", color = DarkGray)
                }

                MessageBubble(
                    type = MessageBubbleViewType.SYSTEM,
                    visibleLevel = MessageVisibleLevel.User
                ) {
                    Text(text = "System message", color = DarkGray)
                }

                MessageBubble(
                    type = MessageBubbleViewType.OTHERS,
                    visibleLevel = MessageVisibleLevel.User
                ) {
                    Text(text = "Message 3", color = DarkGray)
                }

                MessageBubble(
                    type = MessageBubbleViewType.OTHERS,
                    visibleLevel = MessageVisibleLevel.Developer
                ) {
                    Text(text = "Message 3", color = DarkGray)
                }
            }
        }
    }
}
