package jp.co.nintendo.chat.ui.impl.channel.viewdata

import jp.co.nintendo.chat.ui.impl.context.viewdata.message.MessageContextViewData
import jp.co.nintendo.chat.ui.impl.channel.compose.ChatChannelScreen

/**
 * A view data class representing entire view state in [ChatChannelScreen]
 */
sealed interface ChatChannelScreenViewState {
    data object Initializing : ChatChannelScreenViewState
    data object Invalid : ChatChannelScreenViewState
    data class Active(
        val bottomSheetType: ChatChannelBottomSheetType,
        val userDecisionViewData: UserDecisionViewData,
        val progressIndicateViewData: ChatProgressIndicateViewData,
        val inputViewData: ChatChannelInputViewData,
        val messageContextViewData: MessageContextViewData,
    ) : ChatChannelScreenViewState
}