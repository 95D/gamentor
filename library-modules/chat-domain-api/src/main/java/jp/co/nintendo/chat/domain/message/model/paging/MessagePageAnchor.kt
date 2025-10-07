package jp.co.nintendo.chat.domain.message.model.paging

/**
 * Anchor data that serves as a reference for messages loaded per page by paging
 */
sealed interface MessagePageAnchor {
    /**
     * A [MessagePageAnchor] for latest messages
     */
    data object Latest : MessagePageAnchor

    /**
     * A [MessagePageAnchor] for messages around message with messageId
     */
    data class Around(val messageId: String) : MessagePageAnchor
}
