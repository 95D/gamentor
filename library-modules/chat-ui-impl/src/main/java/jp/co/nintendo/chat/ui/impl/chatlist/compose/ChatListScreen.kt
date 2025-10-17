package jp.co.nintendo.chat.ui.impl.chatlist.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import jp.co.nintendo.chat.ui.impl.chatlist.compose.bottomsheet.ChatListBottomSheet
import jp.co.nintendo.chat.ui.impl.chatlist.viewdata.ChatChannelContentKey
import jp.co.nintendo.chat.ui.impl.chatlist.viewmodel.ChatListViewModel
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors
import jp.co.nintendo.design.system.ui.NdsListScreenAppBar
import kotlinx.coroutines.launch
import jp.co.nintendo.multi.lang.resources.R as MultiLangR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    isExpandedScreen: Boolean,
    selectedChannel: ChatChannelContentKey?,
    onNavigateToChatChannel: suspend (ChatChannelContentKey) -> Unit,
    onBackClicked: () -> Unit,
    chatListViewModel: ChatListViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            NdsListScreenAppBar(
                isExpandedScreen = isExpandedScreen,
                title = selectedChannel?.displayChannelName ?: stringResource(
                    MultiLangR.string.title_chat_list
                ),
                onBackClicked = onBackClicked,
                isDetailSelected = selectedChannel != null
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ChatListScreenContent(chatListViewModel, selectedChannel, onNavigateToChatChannel)
        }
    }
}

@Composable
fun ChatListScreenContent(
    chatListViewModel: ChatListViewModel,
    selectedChannel: ChatChannelContentKey?,
    onNavigateToChatChannel: suspend (ChatChannelContentKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenViewState by chatListViewModel.chatListScreenViewStateFlow
        .collectAsState()
    val chatChannelLazyPagingItems =
        chatListViewModel.chatChannelPagingStateFlow
            .collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val semanticColors = LocalAppSemanticColors.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            LazyColumn(
                modifier = Modifier.weight(1.0f),
                state = listState
            ) {
                items(
                    count = chatChannelLazyPagingItems.itemCount,
                    key = chatChannelLazyPagingItems.itemKey { it.channelId }
                ) { index ->
                    val item = chatChannelLazyPagingItems[index]
                    if (item != null) {
                        ChatChannelRow(
                            channel = item,
                            isSelected = item == selectedChannel,
                            onClickItem = { clickedItem ->
                                coroutineScope.launch {
                                    onNavigateToChatChannel(clickedItem)
                                }
                            },
                            onLongClickItem = { clickedItem ->
                                chatListViewModel.openChannelContextActionSuggestion(
                                    clickedItem.channelId
                                )
                            }
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        val channel = chatListViewModel.registerChannel()
                            ?: return@launch
                        onNavigateToChatChannel(channel)
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = semanticColors.buttonPrimary)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.Chat,
                    tint = semanticColors.buttonPrimaryText,
                    contentDescription = stringResource(MultiLangR.string.content_description_new_chat)
                )
            }
        }
    }
    ChatListBottomSheet(
        bottomSheetType = screenViewState.bottomSheetType,
        channelContextViewData = screenViewState.channelContextViewData,
        onDismissBottomSheet = chatListViewModel::dismissBottomSheet,
        onSelectChannelContextAction = chatListViewModel::selectChannelContextAction
    )
}
