package jp.co.nintendo.chat.ui.impl.context.viewdata.channel

import jp.co.nintendo.chat.ui.impl.context.viewdata.ChatContextActionType

/**
 * A view data class representing channel context menu
 */
sealed interface ChannelContextViewData {
    data object None : ChannelContextViewData
    data class SuggestActions(
        val channelId: String,
        val contextActions: List<ChatContextActionType>
    ) : ChannelContextViewData
}