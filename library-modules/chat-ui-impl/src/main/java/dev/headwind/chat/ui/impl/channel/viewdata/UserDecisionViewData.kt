package dev.headwind.chat.ui.impl.channel.viewdata

import androidx.annotation.StringRes

sealed interface UserDecisionViewData {
    data object None : UserDecisionViewData
    data class UserApprove(@param:StringRes val title: Int) : UserDecisionViewData
}