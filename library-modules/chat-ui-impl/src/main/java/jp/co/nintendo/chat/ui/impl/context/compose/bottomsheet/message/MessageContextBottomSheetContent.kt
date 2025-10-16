package jp.co.nintendo.chat.ui.impl.context.compose.bottomsheet.message

import androidx.compose.runtime.Composable
import jp.co.nintendo.chat.ui.impl.context.compose.bottomsheet.ChatContextSuggestGridView
import jp.co.nintendo.chat.ui.impl.context.viewdata.ChatContextActionType
import jp.co.nintendo.chat.ui.impl.context.viewdata.message.MessageContextViewData

@Composable
fun MessageContextBottomSheetContent(
    viewData: MessageContextViewData,
    onSelectMessageContextAction: (ChatContextActionType) -> Unit
) {
    when (viewData) {
        MessageContextViewData.None -> Unit
        is MessageContextViewData.SuggestActions -> ChatContextSuggestGridView(
            viewData.contextActions,
            onSelectMessageContextAction
        )
    }
}