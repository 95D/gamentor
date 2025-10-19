package jp.co.nintendo.setting.ui.impl.app.entry

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import jakarta.inject.Inject
import jp.co.nintendo.setting.ui.entry.app.AppSettingEntry
import jp.co.nintendo.setting.ui.impl.app.compose.AppSettingScreen
import jp.co.nintendo.setting.ui.impl.app.compose.DetailSettingScreen
import jp.co.nintendo.ui.core.compose.adaptive.AppNavigableListDetailPaneScaffold

/**
 * An implementation of [AppSettingEntry]
 */
class AppSettingEntryImpl @Inject constructor() : AppSettingEntry {
    override val route: String = APP_SETTING_ROUTE
    private val arguments: List<NamedNavArgument> = emptyList()

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun attachScreen(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        isExpandedScreen: Boolean
    ) {
        navGraphBuilder.composable(
            route = route,
            arguments = arguments
        ) { backStackEntry ->
            AppNavigableListDetailPaneScaffold(
                listPane = {
                    AppSettingScreen(
                        isExpandedScreen = isExpandedScreen,
                        selectedContentKey = it.selectedContentKey,
                        onNavigateToSettingItem = it.onNavigateToDetail,
                        onBackClicked = it.onBack
                    )
                },
                detailPane = {
                    DetailSettingScreen(
                        isExpandedScreen = isExpandedScreen,
                        selectedItem = it.selectedContentKey,
                        onBackClicked = it.onBack
                    )
                }
            )
        }
    }

    override fun navigate(navController: NavController) {
        navController.navigate(route)
    }

    private companion object {
        const val APP_SETTING_ROUTE = "app_setting"
    }
}