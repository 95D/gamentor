package dev.headwind.chat.data.repository.impl.channel.factory

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.headwind.chat.data.repository.impl.time.SystemCurrentMillisCalculator
import dev.headwind.chat.data.source.local.channel.entity.ChatChannelEntity
import dev.headwind.id.domain.factory.EntityIdFactory
import dev.headwind.id.domain.model.DomainCode
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import kotlin.test.assertEquals

/**
 * An unit test for [ChatChannelEntityFactory]
 */
@RunWith(AndroidJUnit4::class)
class ChatChannelEntityFactoryTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var idFactory: EntityIdFactory

    @Mock
    private lateinit var systemCurrentMillisCalculator: SystemCurrentMillisCalculator

    private lateinit var target: ChatChannelEntityFactory

    @Before
    fun setUp() {
        target = ChatChannelEntityFactory(
            idFactory,
            systemCurrentMillisCalculator
        )
    }

    @Test
    fun `Create channel entity`() {
        whenever(idFactory.create(DomainCode.ChatChannel))
            .doReturn("CHATCH01")
        whenever(systemCurrentMillisCalculator.getCurrentMillis())
            .doReturn(1760364000000L)

        assertEquals(
            ChatChannelEntity(
                channelId = "CHATCH01",
                displayName = "2025.10.13-23:00:00"
            ),
            target.create()
        )
    }
}