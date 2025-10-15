package jp.co.nintendo.chat.ui.impl.channel.viewdata

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface ChatChannelInputViewData {
    data object SendMessage: ChatChannelInputViewData {
        val icon: ImageVector = Icons.AutoMirrored.Default.Send
    }
    data object ContinueToolProcess: ChatChannelInputViewData{
        val icon: ImageVector = Icons.Default.Refresh
    }
    data object Block: ChatChannelInputViewData
}