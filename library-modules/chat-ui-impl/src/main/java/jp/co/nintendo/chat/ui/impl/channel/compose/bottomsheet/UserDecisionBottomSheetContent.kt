package jp.co.nintendo.chat.ui.impl.channel.compose.bottomsheet

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
import jp.co.nintendo.automation.model.tool.decision.UserDecisionResult
import jp.co.nintendo.chat.ui.impl.channel.viewdata.UserDecisionViewData
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors
import jp.co.nintendo.multi.lang.resources.R as MultiLangR

@Composable
fun UserDecisionBottomSheetContent(
    viewData: UserDecisionViewData,
    onConfirmUserDecision: (UserDecisionResult) -> Unit,
    modifier: Modifier = Modifier
) {
    when (viewData) {
        UserDecisionViewData.None -> Unit
        is UserDecisionViewData.UserApprove -> UserApproveView(
            viewData,
            onConfirmUserDecision,
            modifier
        )
    }
}

@Composable
private fun UserApproveView(
    viewData: UserDecisionViewData.UserApprove,
    onConfirmUserDecision: (UserDecisionResult) -> Unit,
    modifier: Modifier
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
                    .padding(horizontal = 16.dp)
                    .clickable {
                        onConfirmUserDecision(UserDecisionResult.Approve(true))
                    }
            )

            Text(
                text = stringResource(MultiLangR.string.confirm_no),
                color = semanticColors.textAccent,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        onConfirmUserDecision(UserDecisionResult.Approve(false))
                    }
            )
        }
    }
}