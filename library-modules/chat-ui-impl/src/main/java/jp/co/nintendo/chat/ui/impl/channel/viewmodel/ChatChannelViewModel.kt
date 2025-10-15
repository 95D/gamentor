package jp.co.nintendo.chat.ui.impl.channel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.nintendo.automation.domain.tool.model.ToolCall
import jp.co.nintendo.automation.domain.tool.model.ToolReturn
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecision
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecisionResult
import jp.co.nintendo.automation.domain.tool.model.lifecycle.ProcessToolLifecycle
import jp.co.nintendo.automation.domain.tool.stateholder.ProcessToolStateHolder
import jp.co.nintendo.automation.domain.tool.stateholder.factory.ProcessToolStateHolderFactory
import jp.co.nintendo.chat.domain.channel.repository.ChatChannelRepository
import jp.co.nintendo.chat.domain.message.model.ChatMessage
import jp.co.nintendo.chat.domain.message.model.ChatMessageRequest
import jp.co.nintendo.chat.domain.message.model.content.MessageContent
import jp.co.nintendo.chat.domain.message.model.content.TextContent
import jp.co.nintendo.chat.domain.message.model.content.ToolProcessContent
import jp.co.nintendo.chat.domain.message.model.extras.AiAssistantExtras
import jp.co.nintendo.chat.domain.message.model.extras.AppOwnerExtras
import jp.co.nintendo.chat.domain.message.model.extras.MessageSenderExtras
import jp.co.nintendo.chat.domain.message.model.extras.SystemExtras
import jp.co.nintendo.chat.domain.message.model.lifecycle.MessageExchangeLifecycle
import jp.co.nintendo.chat.domain.message.model.paging.MessagePageAnchor
import jp.co.nintendo.chat.domain.message.repository.ChatMessageRepository
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatChannelInputViewData
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatChannelSnackBarViewData
import jp.co.nintendo.chat.ui.impl.channel.viewmodel.label.ToolLabelProvider
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatChannelViewState
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatMessageProgressViewData
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatMessageViewData
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatProgressIndicateViewData
import jp.co.nintendo.chat.ui.impl.channel.viewdata.MessageBubbleViewType
import jp.co.nintendo.chat.ui.impl.channel.viewdata.MessageVisibleLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ChatChannelViewModel @Inject constructor(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatChannelRepository: ChatChannelRepository,
    private val toolLabelProvider: ToolLabelProvider,
    processToolStateHolderFactory: ProcessToolStateHolderFactory
) : ViewModel() {
    private var messagePageAnchorMutableStateFlow: MutableStateFlow<MessagePageAnchor> =
        MutableStateFlow(
            MessagePageAnchor.Latest(channelId = "")
        )

    private val channelIdStateFlow: StateFlow<String> = messagePageAnchorMutableStateFlow
        .map { it.channelId }.distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ""
        )

    private val channelId: String get() = channelIdStateFlow.value

    val channelNameFlow: Flow<String> = channelIdStateFlow.map {
        chatChannelRepository.selectChannel(it)?.displayName.orEmpty()
    }

    private val processToolStateHolder: ProcessToolStateHolder =
        processToolStateHolderFactory.create()

    private val messageExchangeLifecycleMutableStateFlow:
            MutableStateFlow<MessageExchangeLifecycle> = MutableStateFlow(
        MessageExchangeLifecycle.Idle
    )

    private val latestMessageFlow: Flow<ChatMessage?> = channelIdStateFlow.flatMapLatest {
        chatMessageRepository.observeLatestMessage(it)
    }

    val messageExchangeLifecycleStateFlow: StateFlow<MessageExchangeLifecycle> =
        messageExchangeLifecycleMutableStateFlow.asStateFlow()

    val chatMessagePagingStateFlow: StateFlow<PagingData<ChatMessageViewData>> =
        messagePageAnchorMutableStateFlow.flatMapLatest { anchor ->
            chatMessageRepository.loadMessagePage(anchor)
                .map { paging -> paging.map { it.toViewdata() } }
        }.cachedIn(viewModelScope)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = PagingData.empty()
            )

    val channelViewStateFlow: StateFlow<ChatChannelViewState> = combine(
        channelIdStateFlow,
        messageExchangeLifecycleStateFlow,
        processToolStateHolder.processToolLifecycleStateFlow,
        latestMessageFlow
    ) { channelId, messageExchangeLifecycle, processToolLifecycle, latestMessage ->
        getChatChannelViewState(
            channelId,
            messageExchangeLifecycle,
            processToolLifecycle,
            latestMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ChatChannelViewState.Initializing
    )

    var messagingCycleJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            processToolStateHolder.processToolLifecycleStateFlow.collect { processToolLifecycle ->
                handleProcessToolLifecycle(processToolLifecycle)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            messageExchangeLifecycleStateFlow.collect {
                Timber.d("Collect message lifecycle $it")
                if (it is MessageExchangeLifecycle.Done) {
                    mayProcessTool()
                }
            }
        }
    }

    fun setCurrentChannelId(channelId: String) {
        messagePageAnchorMutableStateFlow.value = MessagePageAnchor.Latest(channelId)
        viewModelScope.launch {
            mayProcessTool()
        }
    }

    private fun isValidChannelId(): Boolean = channelId.isNotBlank()

    private fun getChatChannelViewState(
        channelId: String,
        messageExchangeLifecycle: MessageExchangeLifecycle,
        processToolLifecycle: ProcessToolLifecycle,
        latestMessage: ChatMessage?
    ): ChatChannelViewState {
        if (channelId.isBlank()) {
            return ChatChannelViewState.Invalid
        }
        val userDecision = (processToolLifecycle as? ProcessToolLifecycle.BlockedByUserDecision)
            ?.userDecision ?: UserDecision.None
        val progressIndicateViewData = getChatProgressIndicateViewData(
            messageExchangeLifecycle,
            processToolLifecycle,
            latestMessage
        )
        val inputViewData = getCurrentInputViewData(
            latestMessage = latestMessage,
            progressIndicateViewData = progressIndicateViewData
        )

        return ChatChannelViewState.Active(
            snackBar = getCurrentSnackBarViewData(userDecision),
            userDecision = userDecision,
            progressIndicateViewData = progressIndicateViewData,
            inputViewData = inputViewData
        )
    }

    private fun getCurrentSnackBarViewData(userDecision: UserDecision): ChatChannelSnackBarViewData {
        return when (userDecision) {
            is UserDecision.Approve -> ChatChannelSnackBarViewData.UserApprove(
                title = toolLabelProvider.getUserApproveLabelString(userDecision.label)
            )

            UserDecision.None -> ChatChannelSnackBarViewData.None
        }
    }

    private fun getCurrentInputViewData(
        latestMessage: ChatMessage?,
        progressIndicateViewData: ChatProgressIndicateViewData
    ): ChatChannelInputViewData {
        val shouldProcessTool = latestMessage?.content is ToolProcessContent
        val isSendable = when (progressIndicateViewData) {
            is ChatProgressIndicateViewData.ExchangeComplete,
            is ChatProgressIndicateViewData.None -> true

            is ChatProgressIndicateViewData.ProcessingTool,
            is ChatProgressIndicateViewData.SendingNewMessage,
            is ChatProgressIndicateViewData.StreamingMessage,
            is ChatProgressIndicateViewData.StreamingTool -> false
        }

        return when {
            !isSendable -> ChatChannelInputViewData.Block
            shouldProcessTool -> ChatChannelInputViewData.ContinueToolProcess
            else -> ChatChannelInputViewData.SendMessage
        }
    }

    private fun getChatProgressIndicateViewData(
        messageExchangeLifecycle: MessageExchangeLifecycle,
        processToolLifecycle: ProcessToolLifecycle,
        latestMessage: ChatMessage?
    ): ChatProgressIndicateViewData {
        Timber.d("Next indicator state: $messageExchangeLifecycle, $processToolLifecycle")
        val latestLocalMessageId = latestMessage?.localMessageId
        val processToolIndicateViewData = when (processToolLifecycle) {
            is ProcessToolLifecycle.Done,
            ProcessToolLifecycle.Idle -> ChatProgressIndicateViewData.None(latestLocalMessageId)

            is ProcessToolLifecycle.Process ->
                ChatProgressIndicateViewData.ProcessingTool(
                    latestLocalMessageId,
                    toolLabelProvider.getToolProcessLabelString(processToolLifecycle.label)
                )

            is ProcessToolLifecycle.BlockedByUserDecision ->
                ChatProgressIndicateViewData.ProcessingTool(
                    latestLocalMessageId,
                    toolLabelProvider.getToolProcessLabelString(processToolLifecycle.label)
                )
        }

        if (processToolIndicateViewData !is ChatProgressIndicateViewData.None) {
            return processToolIndicateViewData
        }

        return when (messageExchangeLifecycle) {
            MessageExchangeLifecycle.Failure,
            MessageExchangeLifecycle.Idle ->
                ChatProgressIndicateViewData.None(latestLocalMessageId)

            is MessageExchangeLifecycle.StreamingResponseContent ->
                ChatProgressIndicateViewData.StreamingMessage(
                    latestLocalMessageId,
                    createChatMessageProgressViewData(
                        content = TextContent(rawText = messageExchangeLifecycle.content),
                        senderExtras = messageExchangeLifecycle.senderExtras
                    )
                )

            MessageExchangeLifecycle.StreamingResponseToolRequest ->
                ChatProgressIndicateViewData.StreamingTool(latestLocalMessageId)

            is MessageExchangeLifecycle.Sending ->
                ChatProgressIndicateViewData.SendingNewMessage(latestLocalMessageId)

            MessageExchangeLifecycle.Done ->
                ChatProgressIndicateViewData.ExchangeComplete(latestLocalMessageId)
        }
    }

    private fun createChatMessageProgressViewData(
        content: MessageContent,
        senderExtras: MessageSenderExtras
    ): ChatMessageProgressViewData = ChatMessageProgressViewData(
        bubbleType = getBubbleType(senderExtras),
        visibleLevel = getVisibleLevel(content),
        senderDisplayName = getSenderDisplayName(senderExtras),
        content = content
    )


    private suspend fun mayProcessTool() = withContext(Dispatchers.IO) {
        Timber.d("may process tool")
        if (!isValidChannelId()) {
            return@withContext
        }
        Timber.d("Channel $channelId")
        val message = chatMessageRepository.selectLatestMessage(channelId)
        if (message == null) {
            return@withContext
        }
        Timber.d("Message $message")
        val toolRequestMessageContent = message.content as? ToolProcessContent ?: return@withContext
        if (isAlreadyComplete(toolRequestMessageContent)) {
            Timber.d("Exchange messages")
            exchangeCurrentMessages(channelId)
        } else {
            Timber.d("Update state")
            processToolStateHolder.mayProcessToolCalls(
                channelId = channelId,
                localMessageId = message.localMessageId,
                toolCalls = toolRequestMessageContent.toolCalls.map {
                    ToolCall(
                        toolCallId = it.toolCallId,
                        toolName = it.toolName,
                        argumentsJson = it.argumentsJson
                    )
                },
                toolReturns = toolRequestMessageContent.toolReturns.map {
                    ToolReturn(
                        toolCallId = it.toolCallId,
                        content = it.content
                    )
                }
            )
        }
    }

    private fun isAlreadyComplete(toolProcessContent: ToolProcessContent): Boolean {
        val completeToolCallIds = toolProcessContent.toolReturns.map { it.toolCallId }
        return toolProcessContent.toolCalls.all { completeToolCallIds.contains(it.toolCallId) }
    }

    private suspend fun handleProcessToolLifecycle(processToolLifecycle: ProcessToolLifecycle) {
        Timber.d("Handle process tool life cycle $processToolLifecycle")
        if (!isValidChannelId()) {
            return
        }
        if (processToolLifecycle is ProcessToolLifecycle.Done) {
            val toolReturn = processToolLifecycle.toolReturn.let {
                ToolProcessContent.ToolReturn(
                    toolCallId = it.toolCallId,
                    content = it.content
                )
            }
            val toolProcessContent = chatMessageRepository.selectMessage(
                localMessageId = processToolLifecycle.localMessageId
            )?.content as? ToolProcessContent ?: return

            val newToolProcessContent = toolProcessContent.copy(
                toolReturns = toolProcessContent.toolReturns + toolReturn
            )
            val isSuccess = chatMessageRepository.updateMessageContent(
                channelId = channelId,
                localMessageId = processToolLifecycle.localMessageId,
                messageContent = newToolProcessContent
            )
            processToolStateHolder.finishTool(isSuccess)
            if (isSuccess) {
                mayProcessTool()
            }
        }
    }

    private fun exchangeAppOwnerMessage(
        channelId: String,
        content: MessageContent
    ) {
        messagingCycleJob?.cancel()
        messagingCycleJob = viewModelScope.launch(Dispatchers.IO) {
            chatMessageRepository.exchangeMessage(
                channelId,
                ChatMessageRequest(
                    content,
                    AppOwnerExtras
                )
            ).collect { messageExchangeLifecycle ->
                messageExchangeLifecycleMutableStateFlow.emit(messageExchangeLifecycle)
            }
        }
    }

    private fun exchangeCurrentMessages(channelId: String) {
        messagingCycleJob?.cancel()
        messagingCycleJob = viewModelScope.launch(Dispatchers.IO) {
            chatMessageRepository.exchangeCurrentMessages(channelId)
                .collect { messageExchangeLifecycle ->
                    messageExchangeLifecycleMutableStateFlow.emit(messageExchangeLifecycle)
                }
        }
    }

    fun handleInputAction(message: String) {
        if (message.isBlank()) {
            return
        }
        val inputViewData = (channelViewStateFlow.value as? ChatChannelViewState.Active)
            ?.inputViewData ?: return
        when (inputViewData) {
            ChatChannelInputViewData.Block -> Unit
            ChatChannelInputViewData.ContinueToolProcess ->
                viewModelScope.launch { mayProcessTool() }

            ChatChannelInputViewData.SendMessage ->
                exchangeAppOwnerMessage(channelId, TextContent(message))
        }
    }

    fun handleUserDecision(userDecisionResult: UserDecisionResult) {
        if (!isValidChannelId()) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            processToolStateHolder.mayHandleUserDecision(userDecisionResult)
        }
    }

    private fun getVisibleLevel(content: MessageContent): MessageVisibleLevel = when (content) {
        is ToolProcessContent -> MessageVisibleLevel.Developer
        else -> MessageVisibleLevel.User
    }

    private fun getSimpleDateString(millis: Long): String {
        val date = Date(millis)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }

    private fun getBubbleType(senderExtras: MessageSenderExtras): MessageBubbleViewType =
        when (senderExtras) {
            is AiAssistantExtras -> MessageBubbleViewType.OTHERS
            SystemExtras -> MessageBubbleViewType.SYSTEM
            AppOwnerExtras -> MessageBubbleViewType.MINE
        }

    private fun getSenderDisplayName(senderExtras: MessageSenderExtras): String =
        when (senderExtras) {
            is AiAssistantExtras -> "AI assistant"
            SystemExtras -> "System"
            AppOwnerExtras -> "User"
        }

    private fun ChatMessage.toViewdata(): ChatMessageViewData = ChatMessageViewData(
        localMessageId = localMessageId,
        createdDate = getSimpleDateString(createdAtMillis),
        bubbleType = getBubbleType(senderExtras),
        senderDisplayName = getSenderDisplayName(senderExtras),
        content = content,
        visibleLevel = getVisibleLevel(content)
    )
}
