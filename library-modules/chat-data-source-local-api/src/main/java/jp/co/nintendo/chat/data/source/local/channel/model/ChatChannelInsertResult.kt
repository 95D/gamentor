package jp.co.nintendo.chat.data.source.local.channel.model

/**
 * A result model of operation to insert channel into local storage
 */
sealed interface ChatChannelInsertResult  {
    /**
     * A result of [ChatChannelInsertResult] representing successful result
     */
    data object Success : ChatChannelInsertResult

    /**
     * A result of [ChatChannelInsertResult] representing failure result
     */
    sealed interface Failure : ChatChannelInsertResult {
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
