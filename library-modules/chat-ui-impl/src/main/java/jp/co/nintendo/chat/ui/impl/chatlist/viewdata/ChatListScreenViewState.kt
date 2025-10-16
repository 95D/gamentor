package jp.co.nintendo.chat.ui.impl.chatlist.viewdata

import jp.co.nintendo.chat.ui.impl.chatlist.compose.ChatListScreen
import jp.co.nintendo.chat.ui.impl.context.viewdata.channel.ChannelContextViewData

/**
 * A view data class representing entire view state in [ChatListScreen]
 */
data class ChatListScreenViewState(
    val bottomSheetType: ChatListBottomSheetType,
    val channelContextViewData: ChannelContextViewData
)