package jp.co.nintendo.chat.domain.message.model.paging

/**
 * Anchor data that serves as a reference for messages loaded per page by paging
 */
sealed interface MessagePageAnchor {
    val channelId: String

    /**
     * A [MessagePageAnchor] for latest messages
     */
    data class Latest(override val channelId: String) : MessagePageAnchor

    /**
     * A [MessagePageAnchor] for messages around message with messageId
     */
    data class Around(override val channelId: String, val localMessageId: String) :
        MessagePageAnchor
}
