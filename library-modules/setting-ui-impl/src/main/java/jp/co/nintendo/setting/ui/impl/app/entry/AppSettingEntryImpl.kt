package jp.co.nintendo.setting.ui.impl.app.entry

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import jakarta.inject.Inject
import jp.co.nintendo.setting.ui.entry.AppSettingEntry
import jp.co.nintendo.setting.ui.impl.app.compose.AppSettingScreen
import jp.co.nintendo.setting.ui.impl.app.compose.DetailSettingScreen
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingDetailContentKey
import kotlinx.coroutines.launch

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
            val coroutineScope = rememberCoroutineScope()
            val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<AppSettingDetailContentKey>()
            val selectedContentKey = scaffoldNavigator.currentDestination?.contentKey
            NavigableListDetailPaneScaffold(
                scaffoldNavigator,
                listPane = {
                    AppSettingScreen(
                        isExpandedScreen = isExpandedScreen,
                        selectedContentKey = selectedContentKey,
                        onNavigateToSettingItem = {
                            scaffoldNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                it
                            )
                        },
                        onBackClicked = {
                            coroutineScope.launch { scaffoldNavigator.navigateBack() }
                        }
                    )
                },
                detailPane = {
                    DetailSettingScreen(
                        isExpandedScreen = isExpandedScreen,
                        selectedItem = selectedContentKey,
                        onBackClicked = {
                            coroutineScope.launch { scaffoldNavigator.navigateBack() }
                        }
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