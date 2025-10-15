package jp.co.nintendo.chat.ui.impl.channel.viewdata

import androidx.annotation.StringRes

sealed interface ChatChannelSnackBarViewData {
    data object None : ChatChannelSnackBarViewData
    data class UserApprove(@param:StringRes val title: Int) : ChatChannelSnackBarViewData
}