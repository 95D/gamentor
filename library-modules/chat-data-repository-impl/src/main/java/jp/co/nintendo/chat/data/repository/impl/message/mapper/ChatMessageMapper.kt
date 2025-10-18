package jp.co.nintendo.chat.data.repository.impl.message.mapper

import jp.co.nintendo.chat.data.repository.impl.di.ChatDataRepositoryCommon
import jp.co.nintendo.chat.data.source.local.message.entity.ChatMessageEntity
import jp.co.nintendo.chat.model.message.ChatMessage
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * A mapper class for mapping data models to domain model [ChatMessage]
 */
class ChatMessageMapper @Inject constructor(
    @param:ChatDataRepositoryCommon private val json: Json
) {
    fun mapToDomain(entity: ChatMessageEntity): ChatMessage = ChatMessage(
        localMessageId = entity.localMessageId,
        createdAtMillis = entity.createdAtMillis,
        content = json.decodeFromString(entity.contentJson),
        senderExtras = json.decodeFromString(entity.senderExtrasJson)
    )
}
