package jp.co.nintendo.chat.ui.impl.context.viewdata.message

import jp.co.nintendo.chat.ui.impl.context.viewdata.ChatContextActionType

/**
 * A view data class representing message context menu
 */
sealed interface MessageContextViewData {
    data object None : MessageContextViewData
    data class SuggestActions(
        val localMessageId: String,
        val contextActions: List<ChatContextActionType>
    ) : MessageContextViewData
}