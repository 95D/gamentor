package dev.headwind.ui.core.compose.adaptive

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A parameters bundle for constructing pane composable from [AppNavigableListDetailPaneScaffold]
 */
class PaneContentParameters<T> internal constructor(
    val selectedContentKey: T?,
    val onNavigateToDetail: (T) -> Unit,
    val onBack: () -> Unit
)

/**
 * A composable function to create [NavigableListDetailPaneScaffold]
 * with common utility logics
 *
 * - Navigation state management
 * - Manage coroutine job for navigations
 * - Back button handling
 */
@Composable
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun<T: Parcelable> AppNavigableListDetailPaneScaffold(
    listPane: @Composable ThreePaneScaffoldPaneScope.(
        PaneContentParameters<T>
    ) -> Unit,
    detailPane: @Composable ThreePaneScaffoldPaneScope.(
        PaneContentParameters<T>
    ) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<T>()
    val selectedContentKey = listDetailNavigator.currentDestination?.contentKey
    var navigationJob by remember { mutableStateOf<Job?>(null) }

    val onBackForListDetailNavigation = {
        navigationJob?.cancel()
        navigationJob = coroutineScope.launch {
            listDetailNavigator.navigateBack()
        }
    }
    val contentParameter = PaneContentParameters(
        selectedContentKey = selectedContentKey,
        onNavigateToDetail = { contentKey: T ->
            navigationJob?.cancel()
            navigationJob = coroutineScope.launch {
                listDetailNavigator.navigateTo(
                    ListDetailPaneScaffoldRole.Detail,
                    contentKey
                )
            }
        },
        onBack = onBackForListDetailNavigation
    )

    BackHandler(
        enabled = selectedContentKey != null,
        onBack = onBackForListDetailNavigation
    )

    NavigableListDetailPaneScaffold(
        listDetailNavigator,
        listPane = {
            listPane(contentParameter)
        },
        detailPane = {
            detailPane(contentParameter)
        }
    )
}
