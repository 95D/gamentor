package jp.co.nintendo.chat.ui.chatlist

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

/**
 * A compose entry point for chat channel list screen
 */
interface ChatChannelListEntry {
    val route: String
    fun attachScreen(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController
    )
}

