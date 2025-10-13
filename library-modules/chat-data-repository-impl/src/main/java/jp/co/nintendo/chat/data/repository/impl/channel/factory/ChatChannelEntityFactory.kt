package jp.co.nintendo.chat.data.repository.impl.channel.factory

import jp.co.nintendo.chat.data.repository.impl.time.SystemCurrentMillisCalculator
import jp.co.nintendo.chat.data.source.local.channel.entity.ChatChannelEntity
import jp.co.nintendo.id.domain.factory.EntityIdFactory
import jp.co.nintendo.id.domain.model.DomainCode
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