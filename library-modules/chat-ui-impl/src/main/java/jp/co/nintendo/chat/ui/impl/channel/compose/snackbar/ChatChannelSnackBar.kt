package jp.co.nintendo.chat.ui.impl.channel.compose.snackbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecisionResult
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatChannelSnackBarViewData
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors
import jp.co.nintendo.design.system.ui.NdsSnackBar
import jp.co.nintendo.multi.lang.resources.R as MultiLangR

@Composable
fun ChatChannelSnackBar(
    viewData: ChatChannelSnackBarViewData,
    onConfirmUserDecision: (UserDecisionResult) -> Unit,
    modifier: Modifier = Modifier
) {
    NdsSnackBar(
        isVisible = viewData !is ChatChannelSnackBarViewData.None,
        modifier = modifier
    ) {
        ChatChannelSnackBarContent(viewData, onConfirmUserDecision)
    }
}

@Composable
fun ChatChannelSnackBarContent(
    viewData: ChatChannelSnackBarViewData,
    onConfirmUserDecision: (UserDecisionResult) -> Unit
) {
    when (viewData) {
        ChatChannelSnackBarViewData.None -> Unit
        is ChatChannelSnackBarViewData.UserApprove -> UserDecisionApprove(
            viewData,
            onConfirmUserDecision
        )
    }
}

@Composable
fun UserDecisionApprove(
    viewData: ChatChannelSnackBarViewData.UserApprove,
    onConfirmUserDecision: (UserDecisionResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalAppSemanticColors.current
    Column(
        modifier = modifier
            .padding(16.dp)
            .width(IntrinsicSize.Max)
    ) {
        Text(
            text = stringResource(viewData.title),
            fontWeight = FontWeight.Bold,
            color = semanticColors.textDefault,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(MultiLangR.string.confirm_yes),
                color = semanticColors.textAccent,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        onConfirmUserDecision(UserDecisionResult.Approve(true))
                    }
            )

            Text(
                text = stringResource(MultiLangR.string.confirm_no),
                color = semanticColors.textAccent,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        onConfirmUserDecision(UserDecisionResult.Approve(false))
                    }
            )
        }
    }
}
