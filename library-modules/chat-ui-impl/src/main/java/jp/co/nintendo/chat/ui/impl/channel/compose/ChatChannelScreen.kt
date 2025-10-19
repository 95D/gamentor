package jp.co.nintendo.chat.ui.impl.channel.compose

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import jp.co.nintendo.chat.ui.impl.channel.compose.bottomsheet.ChatChannelBottomSheet
import jp.co.nintendo.chat.ui.impl.channel.compose.progress.ChatProgressIndication
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatChannelScreenViewState
import jp.co.nintendo.chat.ui.impl.channel.viewmodel.ChatChannelViewModel
import jp.co.nintendo.design.system.ui.NdsDetailScreenAppBar
import kotlinx.coroutines.launch

@Composable
fun ChatChannelScreen(
    isExpandedScreen: Boolean,
    channelId: String,
    onBackClicked: () -> Unit,
    chatChannelViewModel: ChatChannelViewModel = hiltViewModel()
) {
    LaunchedEffect(channelId) {
        chatChannelViewModel.setCurrentChannelId(channelId)
    }
    val channelName by chatChannelViewModel.channelNameFlow.collectAsState("")
    val screenViewState by chatChannelViewModel.channelScreenViewStateFlow.collectAsState()
    Scaffold(
        topBar = {
            NdsDetailScreenAppBar(
                isExpandedScreen = isExpandedScreen,
                title = channelName,
                onBackClicked = onBackClicked
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ChatChannelScreenContent(chatChannelViewModel, screenViewState)
        }
    }
}

@Composable
fun ChatChannelScreenContent(
    chatChannelViewModel: ChatChannelViewModel,
    screenViewState: ChatChannelScreenViewState
) {
    when (screenViewState) {
        is ChatChannelScreenViewState.Active -> ChatChannelActiveContentScreen(
            chatChannelViewModel,
            screenViewState
        )

        ChatChannelScreenViewState.Initializing -> ChatChannelPlaceholderScreen()
        ChatChannelScreenViewState.Invalid -> ChatChannelPlaceholderScreen()
    }
}

@Composable
fun ChatChannelPlaceholderScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
        ) {
        }
    }
}

@Composable
fun ChatChannelActiveContentScreen(
    chatChannelViewModel: ChatChannelViewModel,
    screenViewState: ChatChannelScreenViewState.Active,
    modifier: Modifier = Modifier
) {
    val chatMessageLazyPagingItems = chatChannelViewModel.chatMessageViewDataPagingStateFlow
        .collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var userInputText by remember { mutableStateOf("") }

    var isIncludingLatestMessage by remember { mutableStateOf(false) }
    LaunchedEffect(
        screenViewState.progressIndicateViewData,
        chatMessageLazyPagingItems.itemCount
    ) {
        val firstVisibleIndex = listState.firstVisibleItemIndex
        val isCloseByIndex = firstVisibleIndex <= 5
        isIncludingLatestMessage = chatMessageLazyPagingItems.itemSnapshotList.items.any {
            it.localMessageId == screenViewState.progressIndicateViewData.latestLocalMessageId
        }
        if (isIncludingLatestMessage && isCloseByIndex) {
            coroutineScope.launch { listState.animateScrollToItem(0) }
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            LazyColumn(
                reverseLayout = true,
                modifier = Modifier.weight(1.0f),
                state = listState
            ) {
                item {
                    ChatProgressIndication(viewData = screenViewState.progressIndicateViewData)
                }

                items(
                    count = chatMessageLazyPagingItems.itemCount,
                    key = chatMessageLazyPagingItems.itemKey { it.localMessageId }
                ) { index ->
                    val item = chatMessageLazyPagingItems[index]
                    item?.let {
                        ChatMessageRow(
                            message = it,
                            modifier = Modifier.combinedClickable(
                                onLongClick = {
                                    chatChannelViewModel.openMessageContextActionSuggestion(
                                        it.localMessageId
                                    )
                                },
                                onClick = { /* Do nothing */ }
                            )
                        )
                    }
                }
            }

            MessageInputBar(
                inputViewData = screenViewState.inputViewData,
                userInputText = userInputText,
                onUserInputChange = { userInputText = it },
                onClickAction = {
                    chatChannelViewModel.handleInputAction(userInputText)
                    userInputText = ""
                }
            )
        }
        ChatChannelBottomSheet(
            bottomSheetType = screenViewState.bottomSheetType,
            messageContextViewData = screenViewState.messageContextViewData,
            userDecisionViewData = screenViewState.userDecisionViewData,
            onDismissBottomSheet = chatChannelViewModel::dismissBottomSheet,
            onConfirmUserDecision = chatChannelViewModel::handleUserDecision,
            onSelectMessageContextAction = chatChannelViewModel::selectMessageContextAction
        )
    }
}
