package jp.co.nintendo.automation.business.impl.tool.actual

import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.nintendo.automation.model.tool.decision.UserApproveLabel
import jp.co.nintendo.automation.model.tool.decision.UserDecision
import jp.co.nintendo.automation.model.tool.decision.UserDecisionResult
import jp.co.nintendo.setting.data.repository.app.AppSettingRepository
import jp.co.nintendo.setting.model.app.AppSettings
import jp.co.nintendo.setting.model.app.chess.ChessUnit
import jp.co.nintendo.setting.model.app.theme.AppThemeType
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * An unit test for [ReadCurrentChessStateTool]
 */
@RunWith(AndroidJUnit4::class)
class ReadCurrentChessStateToolTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var json: Json

    @Mock
    private lateinit var appSettingRepository: AppSettingRepository

    private lateinit var target: ReadCurrentChessStateTool

    @Before
    fun setUp() {
        target = ReadCurrentChessStateTool(json, appSettingRepository)
    }

    @Test
    fun `Get user decision`() = runTest {
        assertEquals(
            UserDecision.Approve(label = UserApproveLabel.READ_GAME_DATA),
            target.getUserDecision()
        )
    }

    @Test
    fun `Read current chess state from app settings simulation`() = runTest {
        whenever(appSettingRepository.appSettingsFlow).doReturn(
            flowOf(
                AppSettings(
                    appliedThemeType = AppThemeType.DEVICE,
                    isShownAllMessageBubbles = false,
                    simulatedChessUnits = listOf(
                        ChessUnit(
                            unitType = "king",
                            positionKey = "A1",
                            isBlackTeam = true
                        )
                    )
                )
            )
        )
        whenever(
            json.encodeToString(
                any(),
                eq(
                    listOf(
                        ReadCurrentChessStateTool.ChessPiece(
                            team = "black",
                            role = "king",
                            position = "A1"
                        )
                    )
                )
            )
        ).doReturn("{/*output*/}")
        val resultJson = target.handle(
            UserDecisionResult.Approve(isApproved = true),
            toolCallId = "t01",
            argumentsJson = "{}"
        )
        assertEquals(
            "{/*output*/}",
            resultJson
        )
    }

    @Test
    fun `Read current chess state failed by user reject`() = runTest {
        whenever(
            json.encodeToString<Map<String, String>>(
                any(),
                any()
            )
        ).doReturn("{/*failed*/}")
        val resultJson = target.handle(
            UserDecisionResult.Approve(isApproved = false),
            toolCallId = "t01",
            argumentsJson = "{}"
        )
        assertEquals(
            "{/*failed*/}",
            resultJson
        )
    }
}