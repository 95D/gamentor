package jp.co.nintendo.setting.data.repository.impl.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import jp.co.nintendo.setting.data.repository.impl.app.chess.mapper.ChessUnitMapper
import jp.co.nintendo.setting.data.source.local.app.AppSettingLocalDataSource
import jp.co.nintendo.setting.data.source.local.app.model.AppSettingsEntity
import jp.co.nintendo.setting.data.source.local.app.model.chess.ChessUnitEntity
import jp.co.nintendo.setting.model.app.AppSettings
import jp.co.nintendo.setting.model.app.chess.ChessUnit
import jp.co.nintendo.setting.model.app.theme.AppThemeType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import kotlin.test.assertEquals

/**
 * An unit test for [AppSettingRepositoryImpl]
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AppSettingRepositoryImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var localDataSource: AppSettingLocalDataSource

    @Mock
    private lateinit var chessUnitMapper: ChessUnitMapper

    private lateinit var target: AppSettingRepositoryImpl

    @Before
    fun setUp() {
        target = AppSettingRepositoryImpl(localDataSource, chessUnitMapper)
    }

    @Test
    fun `Collect appsSettings`() = runTest {
        val mockChessUnit = mock<ChessUnit>()
        val mockChessUnitEntity = mock<ChessUnitEntity>()
        whenever(chessUnitMapper.mapToDomain(mockChessUnitEntity))
            .doReturn(mockChessUnit)
        whenever(localDataSource.appSettingsEntityFlow).doReturn(
            flowOf(
                AppSettingsEntity(
                    appliedThemeType = "LIGHT",
                    isShownAllMessageBubbles = true,
                    simulatedChessUnits = listOf(mockChessUnitEntity)
                )
            )
        )

        target.appSettingsFlow.test {
            assertEquals(
                AppSettings(
                    appliedThemeType = AppThemeType.LIGHT,
                    isShownAllMessageBubbles = true,
                    simulatedChessUnits = listOf(mockChessUnit)
                ),
                awaitItem()
            )
            awaitComplete()
        }
    }

    @Test
    fun `Update themeType`() = runTest {
        whenever(localDataSource.appSettingsEntityFlow).doReturn(
            flowOf(AppSettingsEntity.DEFAULT)
        )
        val themeToUpdate = AppThemeType.SAKURA
        target.updateApplyThemeType(themeToUpdate)
        verify(localDataSource).update(
            AppSettingsEntity.DEFAULT.copy(appliedThemeType = "SAKURA")
        )
    }

    @Test
    fun `Update isShownAllMessageBubbles`() = runTest {
        whenever(localDataSource.appSettingsEntityFlow).doReturn(
            flowOf(AppSettingsEntity.DEFAULT)
        )
        target.updateIsShownAllMessageBubbles(true)
        verify(localDataSource).update(
            AppSettingsEntity.DEFAULT.copy(isShownAllMessageBubbles = true)
        )
    }

    @Test
    fun `Update chessSimulation`() = runTest {
        whenever(localDataSource.appSettingsEntityFlow).doReturn(
            flowOf(AppSettingsEntity.DEFAULT)
        )
        val mockChessUnit = mock<ChessUnit>()
        val mockChessUnitEntity = mock<ChessUnitEntity>()
        whenever(
            chessUnitMapper.mapToEntity(
                mockChessUnit
            )
        ).doReturn(
            mockChessUnitEntity
        )
        target.updateChessSimulation(listOf(mockChessUnit))
        verify(localDataSource).update(
            AppSettingsEntity.DEFAULT.copy(simulatedChessUnits = listOf(mockChessUnitEntity))
        )
    }
}