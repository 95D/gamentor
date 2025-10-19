package jp.co.nintendo.chat.ui.impl.chatlist

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import jp.co.nintendo.chat.ui.chatlist.ChatListEntry
import jp.co.nintendo.chat.ui.impl.channel.compose.ChatChannelScreen
import jp.co.nintendo.chat.ui.impl.chatlist.compose.ChatListScreen
import jp.co.nintendo.ui.core.compose.adaptive.AppNavigableListDetailPaneScaffold
import javax.inject.Inject

/**
 * An implementation of [ChatListEntry]
 */
class ChatListEntryImpl @Inject constructor() : ChatListEntry {
    override val route: String = CHAT_LIST_ROUTE_BASE
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
                    ChatListScreen(
                        isExpandedScreen = isExpandedScreen,
                        selectedChannel = it.selectedContentKey,
                        onNavigateToChatChannel = it.onNavigateToDetail,
                        onBackClicked = it.onBack
                    )
                },
                detailPane = {
                    ChatChannelScreen(
                        isExpandedScreen = isExpandedScreen,
                        channelId = it.selectedContentKey?.channelId.orEmpty(),
                        onBackClicked = it.onBack
                    )
                }
            )
        }
    }

    override fun navigateAsTop(navController: NavController) {
        navController.navigate(route) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    private companion object {
        const val CHAT_LIST_ROUTE_BASE = "chat_list"
    }
}