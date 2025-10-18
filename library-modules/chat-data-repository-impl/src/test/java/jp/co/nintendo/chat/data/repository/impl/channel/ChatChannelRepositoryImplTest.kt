package jp.co.nintendo.chat.data.repository.impl.channel

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.nintendo.chat.data.repository.impl.channel.factory.ChatChannelEntityFactory
import jp.co.nintendo.chat.data.repository.impl.channel.mapper.ChatChannelMapper
import jp.co.nintendo.chat.data.repository.impl.channel.repository.ChatChannelRepositoryImpl
import jp.co.nintendo.chat.data.source.local.channel.ChatChannelLocalDataSource
import jp.co.nintendo.chat.data.source.local.channel.entity.ChatChannelEntity
import jp.co.nintendo.chat.data.source.local.channel.model.ChatChannelInsertResult
import jp.co.nintendo.chat.model.channel.ChatChannel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import kotlin.test.assertEquals

/**
 * An unit test for [ChatChannelRepositoryImpl]
 */
@RunWith(AndroidJUnit4::class)
class ChatChannelRepositoryImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var channelLocalDataSource: ChatChannelLocalDataSource

    @Mock
    private lateinit var channelMapper: ChatChannelMapper

    @Mock
    private lateinit var chatChannelEntityFactory: ChatChannelEntityFactory

    private lateinit var target: ChatChannelRepositoryImpl

    @Before
    fun setUp() {
        target = ChatChannelRepositoryImpl(
            channelLocalDataSource,
            channelMapper,
            chatChannelEntityFactory
        )
    }

    @Test
    fun `Local channel page`() = runTest {
        val channelEntity =
            ChatChannelEntity(channelId = "test_channel", displayName = "displayName")
        whenever(channelLocalDataSource.selectChannelPagingSource(0))
            .doReturn(flowOf(PagingData.from(listOf(channelEntity))))
        val channel = ChatChannel(channelId = "test_channel", displayName = "displayName")
        whenever(channelMapper.mapToDomain(channelEntity))
            .doReturn(channel)
        val actualList = target.loadChannelPage().asSnapshot()
        assertEquals(
            channel,
            actualList.first()
        )
    }

    @Test
    fun `Select channel by id`() = runTest {
        val channelEntity =
            ChatChannelEntity(channelId = "test_channel", displayName = "displayName")
        whenever(channelLocalDataSource.selectChannel("test_channel"))
            .doReturn(channelEntity)
        val channel = ChatChannel(channelId = "test_channel", displayName = "displayName")
        whenever(channelMapper.mapToDomain(channelEntity))
            .doReturn(channel)
        assertEquals(channel, target.selectChannel("test_channel"))
    }

    @Test
    fun `Insert channel entity success`() = runTest {
        val channelEntity =
            ChatChannelEntity(channelId = "test_channel", displayName = "displayName")
        whenever(chatChannelEntityFactory.create()).doReturn(channelEntity)
        whenever(channelLocalDataSource.insert(channelEntity))
            .doReturn(ChatChannelInsertResult.Success)

        assertEquals(
            "test_channel",
            target.createNewChatChannel()
        )
    }

    @Test
    fun `Insert channel entity failed`() = runTest {
        val channelEntity =
            ChatChannelEntity(channelId = "test_channel", displayName = "displayName")
        whenever(chatChannelEntityFactory.create()).doReturn(channelEntity)
        whenever(channelLocalDataSource.insert(channelEntity))
            .doReturn(ChatChannelInsertResult.Failure.Unknown)

        assertEquals(
            null,
            target.createNewChatChannel()
        )
    }

    @Test
    fun `Delete channel by id`() = runTest {
        target.deleteChannel("test_channel")
        verify(channelLocalDataSource).deleteChannel("test_channel")
    }
}