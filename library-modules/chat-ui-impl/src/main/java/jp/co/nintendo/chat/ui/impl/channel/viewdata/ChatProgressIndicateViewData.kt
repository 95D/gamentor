package jp.co.nintendo.chat.ui.impl.channel.viewdata

import androidx.annotation.StringRes

sealed interface ChatProgressIndicateViewData {
    val latestLocalMessageId: String?

    data class None(override val latestLocalMessageId: String?) : ChatProgressIndicateViewData
    data class SendingNewMessage(
        override val latestLocalMessageId: String?
    ) : ChatProgressIndicateViewData

    data class StreamingMessage(
        override val latestLocalMessageId: String?,
        val message: ChatMessageProgressViewData
    ) : ChatProgressIndicateViewData

    data class StreamingTool(
        override val latestLocalMessageId: String?
    ) : ChatProgressIndicateViewData

    data class ExchangeComplete(
        override val latestLocalMessageId: String?
    ) : ChatProgressIndicateViewData

    data class ProcessingTool(
        override val latestLocalMessageId: String?,
        @param:StringRes val toolLabel: Int
    ) : ChatProgressIndicateViewData
}