package dev.headwind.chat.data.source.local.message.model

/**
 * A result model of operation to insert message into local storage
 */
sealed interface ChatMessageInsertResult {
    /**
     * A result of [ChatMessageInsertResult] representing successful result
     */
    data object Success : ChatMessageInsertResult

    /**
     * A result of [ChatMessageInsertResult] representing failure result
     */
    sealed interface Failure : ChatMessageInsertResult {
        /**
         * A result of [Failure] that cannot be resolved
         */
        data object Unknown : Failure

        /**
         * A result of [Failure] that user's device storage is full
         */
        data object FullDisk : Failure
    }
}
