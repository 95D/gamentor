package jp.co.nintendo.id.infra.impl.factory

import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.nintendo.id.domain.model.DomainCode
import jp.co.nintendo.id.infra.impl.generator.UuidGenerator
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import java.util.UUID
import kotlin.test.assertEquals

/**
 * An unit test for [EntityIdFactoryImpl]
 */
@RunWith(AndroidJUnit4::class)
class EntityIdFactoryImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var uuidGenerator: UuidGenerator

    private lateinit var target: EntityIdFactoryImpl

    @Before
    fun setUp() {
        target = EntityIdFactoryImpl(uuidGenerator)
    }

    @Test
    fun `Create entity id`() {
        val mockDomainCode = mock<DomainCode> {
            on { code } doReturn "TEST"
        }
        val mockUuid = mock<UUID> {
            on { toString() } doReturn "u1001010"
        }
        whenever(uuidGenerator.randomUUID()).doReturn(mockUuid)
        assertEquals(
            "TEST_u1001010",
            target.create(mockDomainCode)
        )
    }
}
