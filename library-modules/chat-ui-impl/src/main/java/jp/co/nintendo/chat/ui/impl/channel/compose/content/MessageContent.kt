package jp.co.nintendo.chat.ui.impl.channel.compose.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jp.co.nintendo.chat.domain.message.model.content.MessageContent
import jp.co.nintendo.chat.domain.message.model.content.SystemErrorContent
import jp.co.nintendo.chat.domain.message.model.content.TextContent
import jp.co.nintendo.chat.domain.message.model.content.ToolProcessContent
import jp.co.nintendo.chat.ui.impl.channel.viewdata.MessageBubbleViewType
import jp.co.nintendo.chat.ui.impl.channel.viewdata.MessageVisibleLevel
import jp.co.nintendo.design.system.colors.SemanticColors
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors
import jp.co.nintendo.design.system.ui.NdsExpandableText
import jp.co.nintendo.multi.lang.resources.R as MultiLangR

@Composable
fun MessageContent(
    type: MessageBubbleViewType,
    visibleLevel: MessageVisibleLevel,
    content: MessageContent
) {
    val semanticColors = LocalAppSemanticColors.current
    when (content) {
        is TextContent -> MessageContentText(
            content.rawText,
            getTextColor(type, visibleLevel, semanticColors)
        )

        is ToolProcessContent -> MessageContentToolLog(
            toolCallLogs = content.toolCalls.map { it.toString() },
            toolReturnLogs = content.toolReturns.map { it.toString() },
            textColor = getTextColor(type, visibleLevel, semanticColors)
        )

        is SystemErrorContent -> MessageContentText(
            rawText = stringResource(MultiLangR.string.system_error_unknown),
            textColor = getTextColor(type, visibleLevel, semanticColors),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MessageContentText(
    rawText: String,
    textColor: Color,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(text = rawText, color = textColor, textAlign = textAlign)
}

@Composable
fun MessageContentToolLog(
    toolCallLogs: List<String>,
    toolReturnLogs: List<String>,
    textColor: Color
) {
    Column {
        NdsExpandableText(text = stringResource(MultiLangR.string.tool_call)) {
            LogTextBox(toolCallLogs, textColor)
        }

        NdsExpandableText(text = stringResource(MultiLangR.string.tool_return)) {
            LogTextBox(toolReturnLogs, textColor)
        }
    }
}

@Composable
private fun LogTextBox(logs: List<String>, textColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
    ) {
        logs.forEachIndexed { index, item ->
            Text(
                text = item,
                color = textColor
            )
            if (index > 0) {
                HorizontalDivider(color = textColor)
            }
        }
    }
}

private fun getTextColor(
    type: MessageBubbleViewType,
    visibleLevel: MessageVisibleLevel,
    semanticColors: SemanticColors
): Color {
    if (visibleLevel == MessageVisibleLevel.Developer) {
        return semanticColors.messageBubbleTextDeveloper
    }
    return when (type) {
        MessageBubbleViewType.MINE,
        MessageBubbleViewType.OTHERS -> semanticColors.messageBubbleTextNormal

        MessageBubbleViewType.SYSTEM -> semanticColors.messageBubbleTextSystem
    }
}
