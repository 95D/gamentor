package jp.co.nintendo.chat.ui.impl.context.compose.bottomsheet.channel

import androidx.compose.runtime.Composable
import jp.co.nintendo.chat.ui.impl.context.compose.bottomsheet.ChatContextSuggestGridView
import jp.co.nintendo.chat.ui.impl.context.viewdata.ChatContextActionType
import jp.co.nintendo.chat.ui.impl.context.viewdata.channel.ChannelContextViewData
import jp.co.nintendo.chat.ui.impl.context.viewdata.message.MessageContextViewData

@Composable
fun ChannelContextBottomSheetContent(
    viewData: ChannelContextViewData,
    onSelectChannelContextAction: (ChatContextActionType) -> Unit
) {
    when (viewData) {
        ChannelContextViewData.None -> Unit
        is ChannelContextViewData.SuggestActions -> ChatContextSuggestGridView(
            viewData.contextActions,
            onSelectChannelContextAction
        )
    }
}