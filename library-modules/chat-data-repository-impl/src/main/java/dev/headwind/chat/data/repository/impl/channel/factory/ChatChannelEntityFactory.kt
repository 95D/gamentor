package dev.headwind.chat.data.repository.impl.channel.factory

import dev.headwind.chat.data.repository.impl.time.SystemCurrentMillisCalculator
import dev.headwind.chat.data.source.local.channel.entity.ChatChannelEntity
import dev.headwind.id.domain.factory.EntityIdFactory
import dev.headwind.id.domain.model.DomainCode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * A factory class for creating [ChatChannelEntity]
 */
class ChatChannelEntityFactory @Inject constructor(
    val idFactory: EntityIdFactory,
    val millisCalculator: SystemCurrentMillisCalculator
) {
    fun create(): ChatChannelEntity {
        val currentMillis = millisCalculator.getCurrentMillis()
        val timestampName = createChatChannelName(currentMillis)
        val channelId = idFactory.create(DomainCode.ChatChannel)
        return ChatChannelEntity(
            channelId = channelId,
            displayName = timestampName
        )
    }

    private fun createChatChannelName(millis: Long): String {
        val date = Date(millis)
        val format = SimpleDateFormat("yyyy.MM.dd-HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }
}