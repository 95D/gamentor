package jp.co.nintendo.chat.ui.impl.chatlist.compose.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.co.nintendo.chat.ui.impl.chatlist.viewdata.ChatListBottomSheetType
import jp.co.nintendo.chat.ui.impl.context.compose.bottomsheet.channel.ChannelContextBottomSheetContent
import jp.co.nintendo.chat.ui.impl.context.viewdata.ChatContextActionType
import jp.co.nintendo.chat.ui.impl.context.viewdata.channel.ChannelContextViewData
import jp.co.nintendo.design.system.ui.NdsModalBottomSheet


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatListBottomSheet(
    bottomSheetType: ChatListBottomSheetType,
    channelContextViewData: ChannelContextViewData,
    onDismissBottomSheet: () -> Unit,
    onSelectChannelContextAction: (ChatContextActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    NdsModalBottomSheet(
        bottomSheetType != ChatListBottomSheetType.NONE,
        onDismissBottomSheet,
        modifier = modifier
    ) {
        Column(modifier = modifier) {
            ChatListBottomSheetContent(
                bottomSheetType,
                channelContextViewData,
                onSelectChannelContextAction
            )
        }
    }
}

@Composable
private fun ChatListBottomSheetContent(
    bottomSheetType: ChatListBottomSheetType,
    channelContextViewData: ChannelContextViewData,
    onSelectChannelContextAction: (ChatContextActionType) -> Unit
) {
    when (bottomSheetType) {
        ChatListBottomSheetType.NONE -> Unit
        ChatListBottomSheetType.CHANNEL_CONTEXT -> ChannelContextBottomSheetContent(
            channelContextViewData,
            onSelectChannelContextAction = onSelectChannelContextAction
        )
    }
}