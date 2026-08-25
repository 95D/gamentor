package dev.headwind.chat.data.source.local.impl.message.dao

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.headwind.chat.data.source.local.impl.db.ChatDatabase
import dev.headwind.chat.data.source.local.impl.message.entity.ChatMessageDbEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * An unit test for [ChatMessageDao]
 */
@RunWith(AndroidJUnit4::class)
class ChatMessageDaoTest {
    private lateinit var database: ChatDatabase
    private lateinit var target: ChatMessageDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ChatDatabase::class.java
        ).allowMainThreadQueries().build()

        target = database.getChatMessageDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createMessage(
        localMessageId: String,
        channelId: String,
        createdAtMillis: Long = System.currentTimeMillis(),
        contentJson: String = "{}",
        senderExtrasJson: String = "{}"
    ) = ChatMessageDbEntity(
        localMessageId = localMessageId,
        channelId = channelId,
        createdAtMillis = createdAtMillis,
        contentJson = contentJson,
        senderExtrasJson = senderExtrasJson
    )

    @Test
    fun `Query observeLatestMessage with no messages`() = runTest {
        val flow = target.observeLatestMessage("channel1")
        val result = flow.first()

        assertNull(result)
    }

    @Test
    fun `Query observeLatestMessage with messages`() = runTest {
        val channelId = "channel1"
        val message1 = createMessage("MSG_01", channelId, createdAtMillis = 1000L)
        val message2 = createMessage("MSG_02", channelId, createdAtMillis = 2000L)
        val message3 = createMessage("MSG_03", channelId, createdAtMillis = 3000L)

        target.insert(message1)
        target.insert(message2)
        target.insert(message3)

        val flow = target.observeLatestMessage(channelId)
        val result = flow.first()

        assertEquals("MSG_03", result?.localMessageId)
    }

    @Test
    fun `Query observeLatestMessage with messages which has same createdAtMillis`() = runTest {
        val channelId = "channel1"
        val message1 = createMessage("MSG_01", channelId, createdAtMillis = 1000L)
        val message2 = createMessage("MSG_02", channelId, createdAtMillis = 1000L)

        target.insert(message1)
        target.insert(message2)

        val flow = target.observeLatestMessage(channelId)
        val result = flow.first()

        assertEquals(
            "MSG_02",
            result?.localMessageId
        )
    }

    @Test
    fun `Query observeLatestMessage with different channel's messages`() = runTest {
        target.insert(createMessage("MSG_01", "channel1", createdAtMillis = 1000L))
        target.insert(createMessage("MSG_02", "channel2", createdAtMillis = 2000L))

        val flow = target.observeLatestMessage("channel1")
        val result = flow.first()

        assertEquals(
            "MSG_01",
            result?.localMessageId
        )
    }

    @Test
    fun `Query selectLatestMessages`() = runTest {
        val channelId = "channel1"
        target.insert(createMessage("MSG_01", channelId, createdAtMillis = 1000L))
        target.insert(createMessage("MSG_02", channelId, createdAtMillis = 2000L))
        target.insert(createMessage("MSG_03", channelId, createdAtMillis = 3000L))

        val results = target.selectLatestMessages(channelId, limit = 3)

        assertEquals(3, results.size)
        assertEquals("MSG_01", results[0].localMessageId)
        assertEquals("MSG_02", results[1].localMessageId)
        assertEquals("MSG_03", results[2].localMessageId)
    }

    @Test
    fun `Query selectLatestMessages respects limit`() = runTest {
        val channelId = "channel1"
        target.insert(createMessage("MSG_01", channelId, createdAtMillis = 1000L))
        target.insert(createMessage("MSG_02", channelId, createdAtMillis = 2000L))
        target.insert(createMessage("MSG_03", channelId, createdAtMillis = 3000L))
        target.insert(createMessage("MSG_04", channelId, createdAtMillis = 4000L))

        val results = target.selectLatestMessages(channelId, limit = 2)

        assertEquals(2, results.size)
        assertEquals("MSG_03", results[0].localMessageId)
        assertEquals("MSG_04", results[1].localMessageId)
    }

    @Test
    fun `Query selectMessage`() = runTest {
        val message = createMessage("MSG_01", "channel1")
        target.insert(message)

        val result = target.selectMessage("MSG_01")

        assertEquals(
            "MSG_01",
            result?.localMessageId
        )
    }

    @Test
    fun `Query selectMessage with invalid id`() = runTest {
        val result = target.selectMessage("nonexistent")

        assertNull(result)
    }

    @Test
    fun `Query countNewerOrEqual`() = runTest {
        val channelId = "channel1"
        target.insert(createMessage("MSG_01", channelId, createdAtMillis = 1000L))
        target.insert(createMessage("MSG_02", channelId, createdAtMillis = 2000L))
        target.insert(createMessage("MSG_03", channelId, createdAtMillis = 3000L))
        target.insert(createMessage("MSG_04", channelId, createdAtMillis = 4000L))

        val count = target.countNewerOrEqual(
            channelId = channelId,
            anchorLocalMessageId = "MSG_02",
            anchorCreatedAt = 2000L
        )

        assertEquals(
            2,
            count
        )
    }

    @Test
    fun `Query insert replaces existing message`() = runTest {
        val message1 = createMessage(
            "MSG_01",
            "channel1", contentJson = "{\"text\":\"original\"}"
        )
        val message2 = createMessage(
            "MSG_01",
            "channel1", contentJson = "{\"text\":\"updated\"}"
        )

        target.insert(message1)
        target.insert(message2)

        val result = target.selectMessage("MSG_01")

        assertEquals("{\"text\":\"updated\"}", result?.contentJson)
    }

    @Test
    fun `Query deleteMessage removes specific message`() = runTest {
        target.insert(createMessage("MSG_01", "channel1"))
        target.insert(createMessage("MSG_02", "channel1"))

        target.deleteMessage("MSG_01")

        assertNull(target.selectMessage("MSG_01"))
        assertNotNull(target.selectMessage("MSG_02"))
    }

    @Test
    fun `Query deleteAllMessagesInChannel removes only channel messages`() = runTest {
        target.insert(createMessage("MSG_01", "channel1"))
        target.insert(createMessage("MSG_02", "channel1"))

        target.deleteAllMessagesInChannel("channel1")

        assertNull(target.selectMessage("MSG_01"))
        assertNull(target.selectMessage("MSG_02"))
    }

    @Test
    fun `Query selectMessagePagingSource returns messages in descending order`() = runTest {
        val channelId = "channel1"
        target.insert(createMessage("MSG_01", channelId, createdAtMillis = 1000L))
        target.insert(createMessage("MSG_02", channelId, createdAtMillis = 2000L))
        target.insert(createMessage("MSG_03", channelId, createdAtMillis = 3000L))

        val pagingSource = target.selectMessagePagingSource(channelId)
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val page = result as PagingSource.LoadResult.Page
        assertEquals(3, page.data.size)
        assertEquals("MSG_03", page.data[0].localMessageId)
        assertEquals("MSG_02", page.data[1].localMessageId)
        assertEquals("MSG_01", page.data[2].localMessageId)
    }
}
