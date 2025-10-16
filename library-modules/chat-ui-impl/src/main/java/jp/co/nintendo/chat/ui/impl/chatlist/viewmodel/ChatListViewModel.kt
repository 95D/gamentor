package jp.co.nintendo.chat.ui.impl.chatlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.nintendo.chat.domain.channel.model.ChatChannel
import jp.co.nintendo.chat.domain.channel.repository.ChatChannelRepository
import jp.co.nintendo.chat.ui.impl.chatlist.compose.ChatListScreen
import jp.co.nintendo.chat.ui.impl.chatlist.viewdata.ChatChannelContentKey
import jp.co.nintendo.chat.ui.impl.chatlist.viewdata.ChatListBottomSheetType
import jp.co.nintendo.chat.ui.impl.chatlist.viewdata.ChatListScreenViewState
import jp.co.nintendo.chat.ui.impl.context.viewdata.ChatContextActionType
import jp.co.nintendo.chat.ui.impl.context.viewdata.channel.ChannelContextViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A view model mediating state of [ChatListScreen]
 */
@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatChannelRepository: ChatChannelRepository
) : ViewModel() {
    val chatChannelPagingStateFlow: StateFlow<PagingData<ChatChannelContentKey>> =
        chatChannelRepository.loadChannelPage()
            .map { paging -> paging.map { it.toContentKey() } }
            .cachedIn(viewModelScope)
            .stateIn(
                viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = PagingData.empty()
            )

    private val channelContextViewDataMutableStateFlow: MutableStateFlow<ChannelContextViewData> =
        MutableStateFlow(ChannelContextViewData.None)

    val chatListScreenViewStateFlow: StateFlow<ChatListScreenViewState> =
        channelContextViewDataMutableStateFlow.map {
            ChatListScreenViewState(
                bottomSheetType = getBottomSheetType(it),
                channelContextViewData = it
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ChatListScreenViewState(
                bottomSheetType = ChatListBottomSheetType.NONE,
                channelContextViewData = ChannelContextViewData.None
            )
        )

    private fun getBottomSheetType(
        channelContextViewData: ChannelContextViewData
    ): ChatListBottomSheetType = when {
        channelContextViewData != ChannelContextViewData.None ->
            ChatListBottomSheetType.CHANNEL_CONTEXT

        else -> ChatListBottomSheetType.NONE
    }

    fun dismissBottomSheet() {
        val activeViewState = chatListScreenViewStateFlow.value
        when (activeViewState.bottomSheetType) {
            ChatListBottomSheetType.CHANNEL_CONTEXT -> {
                channelContextViewDataMutableStateFlow.value = ChannelContextViewData.None
            }

            ChatListBottomSheetType.NONE -> Unit
        }
    }

    fun openChannelContextActionSuggestion(channelId: String) {
        channelContextViewDataMutableStateFlow.value = ChannelContextViewData.SuggestActions(
            channelId = channelId,
            contextActions = ChatContextActionType.entries
        )
    }

    fun selectChannelContextAction(chatContextActionType: ChatContextActionType) {
        val channelId =
            (channelContextViewDataMutableStateFlow.value as? ChannelContextViewData.SuggestActions)
                ?.channelId
        if (channelId.isNullOrEmpty()) {
            return
        }
        channelContextViewDataMutableStateFlow.value = ChannelContextViewData.None
        viewModelScope.launch(Dispatchers.IO) {
            when (chatContextActionType) {
                ChatContextActionType.DELETE -> chatChannelRepository.deleteChannel(channelId)
            }
        }
    }

    suspend fun registerChannel(): ChatChannelContentKey? = withContext(Dispatchers.IO) {
        chatChannelRepository.createNewChatChannel()
            ?.let { chatChannelRepository.selectChannel(it)?.toContentKey() }
    }

    private fun ChatChannel.toContentKey(): ChatChannelContentKey = ChatChannelContentKey(
        channelId = channelId,
        displayChannelName = displayName
    )
}