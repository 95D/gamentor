package dev.headwind.setting.ui.entry.app

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface AppSettingEntry {
    val route: String
    fun attachScreen(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        isExpandedScreen: Boolean
    )

    fun navigate(navController: NavController)
}