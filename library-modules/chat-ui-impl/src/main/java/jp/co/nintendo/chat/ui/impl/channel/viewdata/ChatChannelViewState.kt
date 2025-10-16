package jp.co.nintendo.chat.ui.impl.channel.viewdata

import jp.co.nintendo.automation.domain.tool.model.decision.UserDecision

sealed interface ChatChannelViewState {
    data object Initializing : ChatChannelViewState
    data object Invalid : ChatChannelViewState
    data class Active(
        val snackBar: UserDecisionViewData,
        val userDecision: UserDecision,
        val progressIndicateViewData: ChatProgressIndicateViewData,
        val inputViewData: ChatChannelInputViewData
    ) : ChatChannelViewState
}