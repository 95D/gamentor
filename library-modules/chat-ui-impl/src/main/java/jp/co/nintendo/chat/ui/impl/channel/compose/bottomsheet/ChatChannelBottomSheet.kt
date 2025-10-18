package jp.co.nintendo.chat.ui.impl.channel.compose.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.co.nintendo.automation.model.tool.decision.UserDecisionResult
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatChannelBottomSheetType
import jp.co.nintendo.chat.ui.impl.channel.viewdata.UserDecisionViewData
import jp.co.nintendo.chat.ui.impl.context.compose.bottomsheet.message.MessageContextBottomSheetContent
import jp.co.nintendo.chat.ui.impl.context.viewdata.ChatContextActionType
import jp.co.nintendo.chat.ui.impl.context.viewdata.message.MessageContextViewData
import jp.co.nintendo.design.system.ui.NdsModalBottomSheet

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatChannelBottomSheet(
    bottomSheetType: ChatChannelBottomSheetType,
    messageContextViewData: MessageContextViewData,
    userDecisionViewData: UserDecisionViewData,
    onDismissBottomSheet: () -> Unit,
    onConfirmUserDecision: (UserDecisionResult) -> Unit,
    onSelectMessageContextAction: (ChatContextActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    NdsModalBottomSheet(
        bottomSheetType != ChatChannelBottomSheetType.NONE,
        onDismissBottomSheet,
        modifier = modifier
    ) {
        Column(modifier = modifier) {
            ChatChannelBottomSheetContent(
                bottomSheetType,
                messageContextViewData,
                userDecisionViewData,
                onConfirmUserDecision,
                onSelectMessageContextAction
            )
        }
    }
}

@Composable
private fun ChatChannelBottomSheetContent(
    bottomSheetType: ChatChannelBottomSheetType,
    messageContextViewData: MessageContextViewData,
    userDecisionViewData: UserDecisionViewData,
    onConfirmUserDecision: (UserDecisionResult) -> Unit,
    onSelectMessageContextAction: (ChatContextActionType) -> Unit
) {
    when (bottomSheetType) {
        ChatChannelBottomSheetType.NONE -> Unit
        ChatChannelBottomSheetType.MESSAGE_CONTEXT ->
            MessageContextBottomSheetContent(messageContextViewData, onSelectMessageContextAction)

        ChatChannelBottomSheetType.USE_DECISION ->
            UserDecisionBottomSheetContent(userDecisionViewData, onConfirmUserDecision)
    }
}