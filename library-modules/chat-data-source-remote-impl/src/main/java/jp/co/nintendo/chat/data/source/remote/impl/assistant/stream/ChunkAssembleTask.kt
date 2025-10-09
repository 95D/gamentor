package jp.co.nintendo.chat.data.source.remote.impl.assistant.stream

import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageResponse
import jp.co.nintendo.chat.data.source.remote.assistant.model.AiAssistantExchangeMessageResponse.InProgress.ChoiceAssembleSnapshot
import jp.co.nintendo.chat.data.source.remote.impl.assistant.model.AiAssistantChatResponseChunk
import jp.co.nintendo.chat.data.source.remote.impl.assistant.model.delta.AssembledChoice
import jp.co.nintendo.chat.data.source.remote.impl.assistant.model.delta.AssembledToolCall
import jp.co.nintendo.chat.data.source.remote.impl.assistant.model.delta.ChoiceDelta
import jp.co.nintendo.chat.data.source.remote.impl.assistant.model.delta.ToolCallDelta
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ChoiceDto
import jp.co.nintendo.chat.data.source.remote.assistant.model.dto.ToolCallDto
import jp.co.nintendo.chat.data.source.remote.impl.di.ChatDataRemoteCommon
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * A task class to assemble stream chunk of AI assistant chat's response
 */
class ChunkAssembleTask(@param:ChatDataRemoteCommon private val json: Json) {
    private var completionId: String? = null
    private val collectedChoices: MutableMap<Int, AssembledChoice> = mutableMapOf()

    fun processData(data: String): AiAssistantExchangeMessageResponse = when (data) {
        DATA_DONE -> createResult()
        else -> processDelta(deltaJson = data)
    }

    private fun processDelta(deltaJson: String): AiAssistantExchangeMessageResponse {
        Timber.d("Delta json: $deltaJson")
        val chunk = try {
            json.decodeFromString<AiAssistantChatResponseChunk>(deltaJson)
        } catch (e: SerializationException) {
            Timber.e("SSE delta parsing error: ${e.message}")
            return AiAssistantExchangeMessageResponse.Failure.Unknown
        }
        completionId = chunk.id
        chunk.choices.forEach { processChoiceDelta(it) }
        return AiAssistantExchangeMessageResponse.InProgress(
            responseId = chunk.id,
            choices = chunk.choices.mapNotNull { it.toSnapshot() }
        )
    }

    private fun processChoiceDelta(delta: ChoiceDelta) {
        val index = delta.index ?: return
        val assembled = collectedChoices.getOrPut(
            index
        ) { AssembledChoice() }

        delta.contentDelta?.role?.let { assembled.role = it }
        delta.contentDelta?.content?.let { assembled.content.append(it) }
        delta.contentDelta?.toolCalls?.forEach {
            processToolCallDelta(assembled, it)
        }
        delta.finishReason?.let { assembled.finishReason = it }
    }

    private fun processToolCallDelta(
        assembledChoice: AssembledChoice,
        delta: ToolCallDelta
    ) {
        val index = delta.index ?: return
        val assembled = assembledChoice.toolCalls.getOrPut(index) {
            AssembledToolCall()
        }

        delta.id?.let { assembled.id = it }
        delta.type?.let { assembled.type = it }
        delta.function?.name?.let { assembled.functionName = it }
        delta.function?.arguments?.let { assembled.functionArguments.append(it) }
    }

    private fun createResult(): AiAssistantExchangeMessageResponse {
        val currentResponseId = completionId
        return if (currentResponseId == null) {
            AiAssistantExchangeMessageResponse.Failure.Unknown
        } else {
            AiAssistantExchangeMessageResponse.Done(
                responseId = currentResponseId,
                choices = collectedChoices.toSortedMap().values.map { it.toDto() }
            )
        }
    }

    private fun ChoiceDelta.toSnapshot(): ChoiceAssembleSnapshot? = when {
        index == null -> null
        contentDelta?.toolCalls?.isNotEmpty() == true -> ChoiceAssembleSnapshot.ToolCall
        else -> ChoiceAssembleSnapshot.Content(
            collectedChoices[index]?.content?.toString().orEmpty()
        )
    }

    private fun AssembledChoice.toDto(): ChoiceDto = ChoiceDto(
        role = role,
        content = content.toString(),
        toolCalls = toolCalls.values.mapNotNull { it.toDto() }
    )

    private fun AssembledToolCall.toDto(): ToolCallDto? {
        val nonNullId = id ?: return null
        val nonNullType = type ?: return null
        val nonNullFunctionName = functionName ?: return null
        return ToolCallDto(
            id = nonNullId,
            type = nonNullType,
            function = ToolCallDto.FunctionCall(
                name = nonNullFunctionName,
                arguments = functionArguments.toString()
            )
        )
    }


    private companion object {
        const val DATA_DONE = "[DONE]"
    }
}
