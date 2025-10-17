package jp.co.nintendo.chat.ui.impl.chatlist.compose

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.co.nintendo.chat.ui.impl.chatlist.viewdata.ChatChannelContentKey
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors

@Composable
fun ChatChannelRow(
    channel: ChatChannelContentKey,
    isSelected: Boolean,
    onClickItem: (ChatChannelContentKey) -> Unit,
    onLongClickItem: (ChatChannelContentKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalAppSemanticColors.current
    val textColor = if (isSelected) {
        semanticColors.buttonPrimary
    } else {
        semanticColors.textDefault
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = channel.displayChannelName,
            color = textColor,
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 16.dp)
                .combinedClickable(
                    onClick = { onClickItem(channel) },
                    onLongClick = { onLongClickItem(channel) }
                )
        )
    }
}
