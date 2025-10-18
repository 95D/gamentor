package jp.co.nintendo.chat.data.repository.impl.channel.repository

import androidx.paging.PagingData
import androidx.paging.map
import jakarta.inject.Inject
import jp.co.nintendo.chat.data.repository.channel.ChatChannelRepository
import jp.co.nintendo.chat.data.repository.impl.channel.factory.ChatChannelEntityFactory
import jp.co.nintendo.chat.data.repository.impl.channel.mapper.ChatChannelMapper
import jp.co.nintendo.chat.data.source.local.channel.ChatChannelLocalDataSource
import jp.co.nintendo.chat.data.source.local.channel.model.ChatChannelInsertResult
import jp.co.nintendo.chat.model.channel.ChatChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * An implementation of [ChatChannelRepository]
 */
class ChatChannelRepositoryImpl @Inject constructor(
    private val chatChannelLocalDataSource: ChatChannelLocalDataSource,
    private val chatChannelMapper: ChatChannelMapper,
    private val chatChannelEntityFactory: ChatChannelEntityFactory
) : ChatChannelRepository {
    override fun loadChannelPage(): Flow<PagingData<ChatChannel>> =
        chatChannelLocalDataSource.selectChannelPagingSource(0)
            .map { it.map(chatChannelMapper::mapToDomain) }

    override suspend fun createNewChatChannel(): String? = withContext(Dispatchers.IO) {
        val entity = chatChannelEntityFactory.create()
        val insertResult = chatChannelLocalDataSource.insert(
            entity = entity
        )
        when (insertResult) {
            ChatChannelInsertResult.Failure.FullDisk,
            ChatChannelInsertResult.Failure.Unknown -> null

            ChatChannelInsertResult.Success -> entity.channelId
        }
    }

    override suspend fun selectChannel(channelId: String): ChatChannel? =
        withContext(Dispatchers.IO) {
            chatChannelLocalDataSource.selectChannel(channelId)
                ?.let(chatChannelMapper::mapToDomain)
        }

    override suspend fun deleteChannel(channelId: String) = withContext(Dispatchers.IO) {
        chatChannelLocalDataSource.deleteChannel(channelId)
    }
}