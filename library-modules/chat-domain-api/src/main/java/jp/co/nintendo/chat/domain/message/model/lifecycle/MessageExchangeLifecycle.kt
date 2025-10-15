package jp.co.nintendo.chat.domain.message.model.lifecycle

import jp.co.nintendo.chat.domain.message.model.extras.MessageSenderExtras

/**
 * A sealed interface representing life cycle of message exchange task.
 *
 * Represents each step of sending a message/content to a server and
 * streaming the response when it occurs.
 */
sealed interface MessageExchangeLifecycle {
    data object Idle : MessageExchangeLifecycle
    /**
     * A [MessageExchangeLifecycle] indicating the message is being transmitted to the server
     */
    data class Sending(val sendingLocalMessageId: String) : MessageExchangeLifecycle

    /**
     * A [MessageExchangeLifecycle] indicating a progressive chunk is being received from response
     * which has text answer
     */
    data class StreamingResponseContent(
        val content: String,
        val senderExtras: MessageSenderExtras
    ) : MessageExchangeLifecycle

    /**
     * A [MessageExchangeLifecycle] indicating a progressive chunk is being received from response
     * which has tool request
     */
    data object StreamingResponseToolRequest : MessageExchangeLifecycle

    /**
     * A [MessageExchangeLifecycle] indicating the entire operation is complete
     */
    data object Done : MessageExchangeLifecycle

    /**
     * A [MessageExchangeLifecycle] indicating the operation terminated due to a failure
     */
    data object Failure : MessageExchangeLifecycle
}
