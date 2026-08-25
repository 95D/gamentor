package dev.headwind.chat.ui.impl.chatlist.viewdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatChannelContentKey(
    val channelId: String,
    val displayChannelName: String,
) : Parcelable