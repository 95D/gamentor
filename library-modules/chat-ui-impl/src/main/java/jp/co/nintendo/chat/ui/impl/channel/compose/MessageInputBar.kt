package jp.co.nintendo.chat.ui.impl.channel.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
    val semanticColors = LocalAppSemanticColors.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = semanticColors.surfaceSecondary
    ) {
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .clip(RoundedCornerShape(corner = CornerSize(12.dp)))
                        .background(color = semanticColors.surfacePrimary),
                ) {
                    BasicTextField(
                        value = userInputText,
                        enabled = inputViewData is ChatChannelInputViewData.SendMessage,
                        onValueChange = onUserInputChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 32.dp, max = 96.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = { onClickAction() }
                        )
                    )
                }
                MessageInputAction(
                    inputViewData,
                    onClickAction,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun MessageInputAction(
    inputViewData: ChatChannelInputViewData,
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (inputViewData) {
        is ChatChannelInputViewData.Block -> MessageInputActionProgress()
        is ChatChannelInputViewData.ContinueToolProcess -> MessageInputActionButton(
            inputViewData.icon,
            onClickAction,
            modifier
        )

        is ChatChannelInputViewData.SendMessage -> MessageInputActionButton(
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
            .size(32.dp)
    ) {
        Icon(
            imageVector = icon,
            tint = semanticColors.buttonPrimary,
            contentDescription = stringResource(
                MultiLangR.string.content_description_new_chat
            ),
            modifier = Modifier
                .size(18.dp)
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
            .size(32.dp)
    )
}

