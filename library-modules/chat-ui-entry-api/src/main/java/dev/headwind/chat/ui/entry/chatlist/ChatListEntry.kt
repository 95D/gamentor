package dev.headwind.chat.ui.entry.chatlist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dev.headwind.setting.ui.entry.app.AppSettingEntry

/**
 * A compose entry point for chat channel list screen
 */
interface ChatListEntry {
    val route: String
    fun attachScreen(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        settingEntry: AppSettingEntry,
        isExpandedScreen: Boolean
    )

    fun navigateAsTop(navController: NavController)
}

