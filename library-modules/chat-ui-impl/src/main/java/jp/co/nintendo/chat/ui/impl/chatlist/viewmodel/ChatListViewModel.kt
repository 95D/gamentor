package jp.co.nintendo.chat.ui.impl.chatlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.nintendo.chat.domain.channel.model.ChatChannel
import jp.co.nintendo.chat.domain.channel.repository.ChatChannelRepository
import jp.co.nintendo.chat.ui.impl.chatlist.viewdata.ChatChannelContentKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

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

    suspend fun registerChannel(): ChatChannelContentKey? = withContext(Dispatchers.IO) {
        chatChannelRepository.createNewChatChannel()
            ?.let { chatChannelRepository.selectChannel(it)?.toContentKey() }
    }

    private fun ChatChannel.toContentKey(): ChatChannelContentKey = ChatChannelContentKey(
        channelId = channelId,
        displayChannelName = displayName
    )
}