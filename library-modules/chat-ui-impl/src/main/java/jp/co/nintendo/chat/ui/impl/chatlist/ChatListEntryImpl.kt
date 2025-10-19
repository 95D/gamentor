package jp.co.nintendo.chat.ui.impl.chatlist

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import jp.co.nintendo.chat.ui.chatlist.ChatListEntry
import jp.co.nintendo.chat.ui.impl.channel.compose.ChatChannelScreen
import jp.co.nintendo.chat.ui.impl.chatlist.compose.ChatListScreen
import jp.co.nintendo.chat.ui.impl.chatlist.viewdata.ChatChannelContentKey
import kotlinx.coroutines.launch
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
            val coroutineScope = rememberCoroutineScope()
            val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<ChatChannelContentKey>()
            val selectedContentKey = scaffoldNavigator.currentDestination?.contentKey
            NavigableListDetailPaneScaffold(
                scaffoldNavigator,
                listPane = {
                    ChatListScreen(
                        isExpandedScreen = isExpandedScreen,
                        selectedChannel = selectedContentKey,
                        onNavigateToChatChannel = {
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
                    ChatChannelScreen(
                        isExpandedScreen = isExpandedScreen,
                        channelId = selectedContentKey?.channelId.orEmpty(),
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
        const val CHAT_LIST_ROUTE_BASE = "chat_list"
    }
}
