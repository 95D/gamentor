package jp.co.nintendo.chat.ui.impl.channel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.nintendo.automation.business.tool.stateholder.ProcessToolStateHolder
import jp.co.nintendo.automation.business.tool.stateholder.factory.ProcessToolStateHolderFactory
import jp.co.nintendo.automation.model.tool.ToolCall
import jp.co.nintendo.automation.model.tool.ToolReturn
import jp.co.nintendo.automation.model.tool.decision.UserDecision
import jp.co.nintendo.automation.model.tool.decision.UserDecisionResult
import jp.co.nintendo.automation.model.tool.lifecycle.ProcessToolLifecycle
import jp.co.nintendo.chat.data.repository.channel.ChatChannelRepository
import jp.co.nintendo.chat.data.repository.message.ChatMessageRepository
import jp.co.nintendo.chat.model.message.ChatMessage
import jp.co.nintendo.chat.model.message.ChatMessageRequest
import jp.co.nintendo.chat.model.message.content.MessageContent
import jp.co.nintendo.chat.model.message.content.TextContent
import jp.co.nintendo.chat.model.message.content.ToolProcessContent
import jp.co.nintendo.chat.model.message.extras.AiAssistantExtras
import jp.co.nintendo.chat.model.message.extras.AppOwnerExtras
import jp.co.nintendo.chat.model.message.extras.MessageSenderExtras
import jp.co.nintendo.chat.model.message.extras.SystemExtras
import jp.co.nintendo.chat.model.message.lifecycle.MessageExchangeLifecycle
import jp.co.nintendo.chat.model.message.paging.MessagePageAnchor
import jp.co.nintendo.chat.ui.impl.channel.compose.ChatChannelScreen
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatChannelBottomSheetType
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatChannelInputViewData
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatChannelScreenViewState
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatMessageProgressViewData
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatMessageViewData
import jp.co.nintendo.chat.ui.impl.channel.viewdata.ChatProgressIndicateViewData
import jp.co.nintendo.chat.ui.impl.channel.viewdata.MessageBubbleViewType
import jp.co.nintendo.chat.ui.impl.channel.viewdata.MessageVisibleLevel
import jp.co.nintendo.chat.ui.impl.channel.viewdata.UserDecisionViewData
import jp.co.nintendo.chat.ui.impl.channel.viewmodel.label.ToolLabelProvider
import jp.co.nintendo.chat.ui.impl.context.viewdata.ChatContextActionType
import jp.co.nintendo.chat.ui.impl.context.viewdata.message.MessageContextViewData
import jp.co.nintendo.setting.data.repository.app.AppSettingRepository
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * A view model mediating state of [ChatChannelScreen]
 */
@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ChatChannelViewModel @Inject constructor(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatChannelRepository: ChatChannelRepository,
    appSettingRepository: AppSettingRepository,
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

    private val chatMessagePagingStateFlow: Flow<PagingData<ChatMessage>> =
        messagePageAnchorMutableStateFlow.flatMapLatest { anchor ->
            chatMessageRepository.loadMessagePage(anchor)
        }.cachedIn(viewModelScope)

    val chatMessageViewDataPagingStateFlow: StateFlow<PagingData<ChatMessageViewData>> =
        combine(
            chatMessagePagingStateFlow,
            appSettingRepository.appSettingsFlow
        ) { paging, settings ->
            paging.filter {
                isVisibleMessage(
                    it.content,
                    settings.isShownAllMessageBubbles
                )
            }.map { it.toViewdata() }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PagingData.empty()
        )

    private val messageContextViewDataMutableStateFlow: MutableStateFlow<MessageContextViewData> =
        MutableStateFlow(MessageContextViewData.None)

    val channelScreenViewStateFlow: StateFlow<ChatChannelScreenViewState> = combine(
        channelIdStateFlow,
        messageExchangeLifecycleStateFlow,
        processToolStateHolder.processToolLifecycleStateFlow,
        messageContextViewDataMutableStateFlow,
        latestMessageFlow
    ) { channelId,
        messageExchangeLifecycle,
        processToolLifecycle,
        messageContextViewData,
        latestMessage ->
        getChatChannelScreenViewState(
            channelId,
            messageExchangeLifecycle,
            processToolLifecycle,
            messageContextViewData,
            latestMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ChatChannelScreenViewState.Initializing
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

    private fun getChatChannelScreenViewState(
        channelId: String,
        messageExchangeLifecycle: MessageExchangeLifecycle,
        processToolLifecycle: ProcessToolLifecycle,
        messageContextViewData: MessageContextViewData,
        latestMessage: ChatMessage?
    ): ChatChannelScreenViewState {
        if (channelId.isBlank()) {
            return ChatChannelScreenViewState.Invalid
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

        val userDecisionViewData = getCurrentSnackBarViewData(userDecision)
        val bottomSheetType = selectBottomSheetType(userDecisionViewData, messageContextViewData)
        return ChatChannelScreenViewState.Active(
            bottomSheetType = bottomSheetType,
            userDecisionViewData = userDecisionViewData,
            progressIndicateViewData = progressIndicateViewData,
            inputViewData = inputViewData,
            messageContextViewData = messageContextViewData
        )
    }

    private fun selectBottomSheetType(
        userDecisionViewData: UserDecisionViewData,
        messageContextViewData: MessageContextViewData
    ): ChatChannelBottomSheetType = when {
        userDecisionViewData !is UserDecisionViewData.None ->
            ChatChannelBottomSheetType.USE_DECISION

        messageContextViewData !is MessageContextViewData.None ->
            ChatChannelBottomSheetType.MESSAGE_CONTEXT

        else -> ChatChannelBottomSheetType.NONE
    }

    private fun getCurrentSnackBarViewData(userDecision: UserDecision): UserDecisionViewData {
        return when (userDecision) {
            is UserDecision.Approve -> UserDecisionViewData.UserApprove(
                title = toolLabelProvider.getUserApproveLabelString(userDecision.label)
            )

            UserDecision.None -> UserDecisionViewData.None
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
        if (!isValidChannelId()) {
            return@withContext
        }
        val message = chatMessageRepository.selectLatestMessage(channelId)
        if (message == null) {
            return@withContext
        }
        val toolRequestMessageContent = message.content as? ToolProcessContent ?: return@withContext
        if (isAlreadyComplete(toolRequestMessageContent)) {
            exchangeCurrentMessages(channelId)
        } else {
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
        val inputViewData = (channelScreenViewStateFlow.value as? ChatChannelScreenViewState.Active)
            ?.inputViewData ?: return
        when (inputViewData) {
            ChatChannelInputViewData.Block -> Unit
            ChatChannelInputViewData.ContinueToolProcess ->
                viewModelScope.launch { mayProcessTool() }

            ChatChannelInputViewData.SendMessage -> {
                if (message.isBlank()) {
                    return
                }
                exchangeAppOwnerMessage(channelId, TextContent(message))
            }
        }
    }

    fun dismissBottomSheet() {
        val activeViewState = channelScreenViewStateFlow.value as? ChatChannelScreenViewState.Active ?: return
        when (activeViewState.bottomSheetType) {
            ChatChannelBottomSheetType.MESSAGE_CONTEXT -> {
                messageContextViewDataMutableStateFlow.value = MessageContextViewData.None
            }

            ChatChannelBottomSheetType.USE_DECISION -> handleUserDecisionAsNegative(
                activeViewState.userDecisionViewData
            )

            ChatChannelBottomSheetType.NONE -> Unit
        }
    }

    private fun handleUserDecisionAsNegative(userDecision: UserDecisionViewData) {
        when (userDecision) {
            UserDecisionViewData.None -> Unit
            is UserDecisionViewData.UserApprove -> handleUserDecision(
                UserDecisionResult.Approve(isApproved = false)
            )
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

    fun openMessageContextActionSuggestion(localMessageId: String) {
        messageContextViewDataMutableStateFlow.value = MessageContextViewData.SuggestActions(
            localMessageId = localMessageId,
            contextActions = ChatContextActionType.entries
        )
    }

    fun selectMessageContextAction(chatContextActionType: ChatContextActionType) {
        if (!isValidChannelId()) {
            return
        }
        val localMessageId =
            (messageContextViewDataMutableStateFlow.value as? MessageContextViewData.SuggestActions)
                ?.localMessageId
        if (localMessageId.isNullOrEmpty()) {
            return
        }
        messageContextViewDataMutableStateFlow.value = MessageContextViewData.None
        viewModelScope.launch(Dispatchers.IO) {
            when (chatContextActionType) {
                ChatContextActionType.DELETE -> chatMessageRepository
                    .deleteMessage(localMessageId)
            }
        }
    }

    private fun isVisibleMessage(content: MessageContent, isShownAllMessages: Boolean): Boolean =
        isShownAllMessages || getVisibleLevel(content) == MessageVisibleLevel.User

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
