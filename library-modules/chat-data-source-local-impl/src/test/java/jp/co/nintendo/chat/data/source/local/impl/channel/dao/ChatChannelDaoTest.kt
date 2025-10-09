package jp.co.nintendo.chat.data.source.local.impl.channel.dao

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.nintendo.chat.data.source.local.impl.channel.entity.ChatChannelDbEntity
import jp.co.nintendo.chat.data.source.local.impl.db.ChatDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * An unit test for [ChatChannelDao]
 */
@RunWith(AndroidJUnit4::class)
class ChatChannelDaoTest {
    private lateinit var database: ChatDatabase
    private lateinit var target: ChatChannelDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ChatDatabase::class.java
        ).allowMainThreadQueries().build()

        target = database.getChatChannelDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createChannel(
        channelId: String
    ) = ChatChannelDbEntity(
        channelId = channelId
    )

    @Test
    fun `Query selectChannel with no channels`() = runTest {
        val result = target.selectChannel("channel1")

        assertNull(result)
    }

    @Test
    fun `Query selectChannel with existing channel`() = runTest {
        val channel = createChannel("channel1")
        target.insert(channel)

        val result = target.selectChannel("channel1")

        assertNotNull(result)
        assertEquals("channel1", result.channelId)
    }

    @Test
    fun `Query selectChannel with different channel`() = runTest {
        target.insert(createChannel("channel1"))
        target.insert(createChannel("channel2"))

        val result = target.selectChannel("channel1")

        assertEquals("channel1", result?.channelId)
    }

    @Test
    fun `Query selectChannel with invalid id`() = runTest {
        target.insert(createChannel("channel1"))

        val result = target.selectChannel("nonexistent")

        assertNull(result)
    }

    @Test
    fun `Query insert creates new channel`() = runTest {
        val channel = createChannel("channel1")

        target.insert(channel)

        val result = target.selectChannel("channel1")
        assertNotNull(result)
        assertEquals("channel1", result.channelId)
    }

    @Test
    fun `Query insert replaces existing channel`() = runTest {
        val channel1 = createChannel("channel1")
        val channel2 = createChannel("channel1")

        target.insert(channel1)
        target.insert(channel2)

        val result = target.selectChannel("channel1")
        assertNotNull(result)
        assertEquals("channel1", result.channelId)
    }

    @Test
    fun `Query insert multiple channels`() = runTest {
        target.insert(createChannel("channel1"))
        target.insert(createChannel("channel2"))
        target.insert(createChannel("channel3"))

        assertNotNull(target.selectChannel("channel1"))
        assertNotNull(target.selectChannel("channel2"))
        assertNotNull(target.selectChannel("channel3"))
    }

    @Test
    fun `Query deleteChannel removes specific channel`() = runTest {
        target.insert(createChannel("channel1"))
        target.insert(createChannel("channel2"))

        target.deleteChannel("channel1")

        assertNull(target.selectChannel("channel1"))
        assertNotNull(target.selectChannel("channel2"))
    }

    @Test
    fun `Query deleteChannel with nonexistent channel`() = runTest {
        target.insert(createChannel("channel1"))

        target.deleteChannel("nonexistent")

        assertNotNull(target.selectChannel("channel1"))
    }

    @Test
    fun `Query deleteChannel removes only specified channel`() = runTest {
        target.insert(createChannel("channel1"))
        target.insert(createChannel("channel2"))
        target.insert(createChannel("channel3"))

        target.deleteChannel("channel2")

        assertNotNull(target.selectChannel("channel1"))
        assertNull(target.selectChannel("channel2"))
        assertNotNull(target.selectChannel("channel3"))
    }

    @Test
    fun `Query selectChannelPagingSource returns all channels`() = runTest {
        target.insert(createChannel("channel1"))
        target.insert(createChannel("channel2"))
        target.insert(createChannel("channel3"))

        val pagingSource = target.selectChannelPagingSource()
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val page = result as PagingSource.LoadResult.Page
        assertEquals(3, page.data.size)
    }

    @Test
    fun `Query selectChannelPagingSource with no channels`() = runTest {
        val pagingSource = target.selectChannelPagingSource()
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val page = result as PagingSource.LoadResult.Page
        assertEquals(0, page.data.size)
    }

    @Test
    fun `Query selectChannelPagingSource returns channels with correct ids`() = runTest {
        target.insert(createChannel("channel1"))
        target.insert(createChannel("channel2"))

        val pagingSource = target.selectChannelPagingSource()
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val page = result as PagingSource.LoadResult.Page
        val channelIds = page.data.map { it.channelId }
        assert(channelIds.contains("channel1"))
        assert(channelIds.contains("channel2"))
    }
}
