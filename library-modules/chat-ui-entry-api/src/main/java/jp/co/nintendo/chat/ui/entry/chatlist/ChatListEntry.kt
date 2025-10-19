package jp.co.nintendo.chat.ui.entry.chatlist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

/**
 * A compose entry point for chat channel list screen
 */
interface ChatListEntry {
    val route: String
    fun attachScreen(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        isExpandedScreen: Boolean
    )

    fun navigateAsTop(navController: NavController)
}

