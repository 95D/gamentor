package jp.co.nintendo.chat.ui.impl.channel.viewdata

import jp.co.nintendo.chat.ui.impl.channel.compose.ChatChannelScreen

/**
 * A view data representing bottom sheet type is shown from [ChatChannelScreen]
 */
enum class ChatChannelBottomSheetType {
    NONE,
    MESSAGE_CONTEXT,
    USE_DECISION
}