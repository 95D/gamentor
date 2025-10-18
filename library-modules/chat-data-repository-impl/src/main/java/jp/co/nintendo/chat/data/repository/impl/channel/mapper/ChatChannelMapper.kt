package jp.co.nintendo.chat.data.repository.impl.channel.mapper

import jp.co.nintendo.chat.data.source.local.channel.entity.ChatChannelEntity
import jp.co.nintendo.chat.model.channel.ChatChannel
import javax.inject.Inject

/**
 * A mapper class for mapping data models to domain model [ChatChannel]
 */
class ChatChannelMapper @Inject constructor() {
    fun mapToDomain(entity: ChatChannelEntity): ChatChannel = ChatChannel(
        channelId = entity.channelId,
        displayName = entity.displayName
    )
}