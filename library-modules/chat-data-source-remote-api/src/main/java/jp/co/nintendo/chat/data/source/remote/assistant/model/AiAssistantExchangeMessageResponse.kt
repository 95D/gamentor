package jp.co.nintendo.chat.data.source.remote.assistant.model

import jp.co.nintendo.chat.data.source.remote.assistant.AiAssistantChatRemoteDataSource
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ChoiceDto

/**
 * A response model to request [AiAssistantChatRemoteDataSource.exchangeMessage]
 */
sealed interface AiAssistantExchangeMessageResponse {
    data class InProgress(val responseId: String, val choices: List<ChoiceAssembleSnapshot>) :
        AiAssistantExchangeMessageResponse {
        sealed interface ChoiceAssembleSnapshot {
            data class Content(val assembledContent: String) : ChoiceAssembleSnapshot
            data object ToolCall : ChoiceAssembleSnapshot
        }
    }

    data class Done(
        val responseId: String,
        val choices: List<ChoiceDto>
    ) : AiAssistantExchangeMessageResponse

    sealed interface Failure : AiAssistantExchangeMessageResponse {
        data object Unknown : Failure
        data class Response(val code: Int, val message: String) : Failure
    }
}
