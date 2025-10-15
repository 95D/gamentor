package jp.co.nintendo.chat.ui.impl.channel.compose

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.window.core.layout.WindowSizeClass
import jp.co.nintendo.chat.ui.impl.channel.compose.progress.ChatProgressIndication
import jp.co.nintendo.chat.ui.impl.channel.compose.snackbar.ChatChannelSnackBar
import jp.co.nintendo.chat.ui.impl.channel.viewmodel.ChatChannelViewModel
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatChannelViewState
import jp.co.nintendo.design.system.ui.NdsDetailScreenAppBar
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun ChatChannelScreen(
    channelId: String,
    onBackClicked: () -> Unit,
    viewModel: ChatChannelViewModel = hiltViewModel()
) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = windowAdaptiveInfo.windowSizeClass
    val isExpandedScreen = windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )

    LaunchedEffect(channelId) {
        viewModel.setCurrentChannelId(channelId)
    }
    val channelName by viewModel.channelNameFlow.collectAsState("")
    val viewState by viewModel.channelViewStateFlow.collectAsState()
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
            ChatChannelScreenContent(viewModel, viewState)
        }
    }
}

@Composable
fun ChatChannelScreenContent(
    viewModel: ChatChannelViewModel,
    viewState: ChatChannelViewState
) {
    when (viewState) {
        is ChatChannelViewState.Active -> ChatChannelActiveContentScreen(
            viewModel,
            viewState
        )

        ChatChannelViewState.Initializing -> ChatChannelPlaceholderScreen()
        ChatChannelViewState.Invalid -> ChatChannelPlaceholderScreen()
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
    viewModel: ChatChannelViewModel,
    state: ChatChannelViewState.Active,
    modifier: Modifier = Modifier
) {
    val chatMessageLazyPagingItems = viewModel.chatMessagePagingStateFlow
        .collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var userInputText by remember { mutableStateOf("") }

    var isIncludingLatestMessage by remember { mutableStateOf(false) }
    LaunchedEffect(
        state.progressIndicateViewData,
        chatMessageLazyPagingItems.itemCount
    ) {
        val firstVisibleIndex = listState.firstVisibleItemIndex
        val isCloseByIndex = firstVisibleIndex <= 5
        isIncludingLatestMessage = chatMessageLazyPagingItems.itemSnapshotList.items.any {
            it.localMessageId == state.progressIndicateViewData.latestLocalMessageId
        }
        if (isIncludingLatestMessage && isCloseByIndex) {
            coroutineScope.launch { listState.animateScrollToItem(0) }
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding(),
            verticalArrangement = Arrangement.Top
        ) {
            LazyColumn(
                reverseLayout = true,
                modifier = Modifier.weight(1.0f),
                state = listState
            ) {
                item {
                    ChatProgressIndication(viewData = state.progressIndicateViewData)
                }

                items(
                    count = chatMessageLazyPagingItems.itemCount,
                    key = chatMessageLazyPagingItems.itemKey { it.localMessageId }
                ) { index ->
                    val item = chatMessageLazyPagingItems[index]
                    item?.let {
                        ChatMessageRow(message = it, modifier = Modifier.clickable { })
                    }
                }
            }

            MessageInputBar(
                inputViewData = state.inputViewData,
                userInputText = userInputText,
                onUserInputChange = { userInputText = it },
                onClickAction = {
                    viewModel.handleInputAction(userInputText)
                    userInputText = ""
                }
            )
        }
        ChatChannelSnackBar(
            viewData = state.snackBar,
            onConfirmUserDecision = viewModel::handleUserDecision,
            modifier = Modifier.padding()
                .navigationBarsPadding()
                .imePadding()
        )
    }
}
