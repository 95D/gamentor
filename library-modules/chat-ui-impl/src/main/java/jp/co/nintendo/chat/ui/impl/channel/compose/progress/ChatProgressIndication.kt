package jp.co.nintendo.chat.ui.impl.channel.compose.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jp.co.nintendo.chat.ui.impl.channel.compose.MessageBubble
import jp.co.nintendo.chat.ui.impl.channel.compose.content.MessageContent
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatProgressIndicateViewData
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors
import jp.co.nintendo.multi.lang.resources.R as MultiLangR

@Composable
fun ChatProgressIndication(
    viewData: ChatProgressIndicateViewData,
    modifier: Modifier = Modifier
) {
    when (viewData) {
        is ChatProgressIndicateViewData.None,
        is ChatProgressIndicateViewData.ExchangeComplete,
        is ChatProgressIndicateViewData.SendingNewMessage -> Unit
        is ChatProgressIndicateViewData.ProcessingTool -> ProcessingTool(viewData, modifier)
        is ChatProgressIndicateViewData.StreamingMessage -> StreamingMessage(viewData, modifier)
        is ChatProgressIndicateViewData.StreamingTool -> StreamingTool(modifier)
    }
}

@Composable
fun ProcessingTool(
    viewData: ChatProgressIndicateViewData.ProcessingTool,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(2.dp),
            strokeWidth = 2.dp
        )
        Text(
            text = stringResource(id = viewData.toolLabel),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun StreamingTool(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(2.dp),
            strokeWidth = 2.dp
        )
        Text(
            text = stringResource(id = MultiLangR.string.streaming_tool),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun StreamingMessage(
    viewData: ChatProgressIndicateViewData.StreamingMessage,
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalAppSemanticColors.current
    val message = viewData.message
    Column(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = message.senderDisplayName,
            color = semanticColors.textDefault,
        )
        MessageBubble(
            type = message.bubbleType,
            visibleLevel = message.visibleLevel
        ) {
            MessageContent(message.bubbleType, message.visibleLevel, message.content)
        }
    }
}