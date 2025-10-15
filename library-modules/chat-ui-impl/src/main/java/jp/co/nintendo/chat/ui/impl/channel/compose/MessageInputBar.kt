package jp.co.nintendo.chat.ui.impl.channel.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatChannelInputViewData
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors
import jp.co.nintendo.multi.lang.resources.R as MultiLangR

@Composable
fun MessageInputBar(
    inputViewData: ChatChannelInputViewData,
    userInputText: String,
    onUserInputChange: (String) -> Unit,
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFE0E0E0),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = userInputText,
                enabled = inputViewData is ChatChannelInputViewData.SendMessage,
                onValueChange = onUserInputChange,
                modifier = Modifier
                    .weight(1.0f)
                    .padding(end = 8.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSend = { onClickAction() }
                )
            )
            MessageInputAction(inputViewData, onClickAction)
        }
    }
}

@Composable
fun MessageInputAction(
    inputViewData: ChatChannelInputViewData,
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    when(inputViewData) {
        is ChatChannelInputViewData.Block -> MessageInputActionProgress()
        is ChatChannelInputViewData.ContinueToolProcess -> MessageInputActionButton(
            inputViewData.icon,
            onClickAction,
            modifier
        )
        is ChatChannelInputViewData.SendMessage ->  MessageInputActionButton(
            inputViewData.icon,
            onClickAction,
            modifier
        )
    }
}

@Composable
fun MessageInputActionButton(
    icon: ImageVector,
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalAppSemanticColors.current
    IconButton(
        onClick = onClickAction,
        modifier = modifier
            .clip(CircleShape)
            .background(color = semanticColors.buttonPrimaryText)
            .width(48.dp)
            .height(48.dp)
    ) {
        Icon(
            imageVector = icon,
            tint = semanticColors.buttonPrimary,
            contentDescription = stringResource(
                MultiLangR.string.content_description_new_chat
            )
        )
    }
}

@Composable
fun MessageInputActionProgress(
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalAppSemanticColors.current
    CircularProgressIndicator(
        modifier = modifier
            .clip(CircleShape)
            .background(color = semanticColors.buttonPrimaryText)
            .width(48.dp)
            .height(48.dp)
    )
}

