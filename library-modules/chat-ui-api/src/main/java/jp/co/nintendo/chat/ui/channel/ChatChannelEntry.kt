package jp.co.nintendo.chat.ui.channel

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

/**
 * A compose entry point for chat channel screen
 */
interface ChatChannelEntry {
    fun attachScreen(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController
    )

    fun navigate(navController: NavController, channelId: String)
}
