package jp.co.nintendo.automation.business.impl.tool.actual

import jp.co.nintendo.automation.model.tool.ToolParameterSignature
import jp.co.nintendo.automation.model.tool.ToolProcessLabel
import jp.co.nintendo.automation.model.tool.ToolSignature
import jp.co.nintendo.automation.model.tool.decision.UserApproveLabel
import jp.co.nintendo.automation.model.tool.decision.UserDecision
import jp.co.nintendo.automation.model.tool.decision.UserDecisionResult
import jp.co.nintendo.automation.business.impl.di.AutomationDomainCommon
import jp.co.nintendo.automation.business.impl.tool.Tool
import jp.co.nintendo.automation.business.impl.tool.ToolFactory
import jp.co.nintendo.automation.business.impl.tool.model.ToolCallState
import jp.co.nintendo.setting.data.repository.app.AppSettingRepository
import jp.co.nintendo.setting.model.app.chess.ChessUnit
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * A [Tool] class for providing current chess game information to AI Assistant
 */
class ReadCurrentChessStateTool(
    private val json: Json,
    private val appSettingRepository: AppSettingRepository
) : Tool {
    override val labelBeforeStart: ToolProcessLabel
        get() = ToolProcessLabel.PREPARING_TOOL

    override val labelBeforeDecide: ToolProcessLabel
        get() = ToolProcessLabel.REQUESTING_APPROVE

    override val labelBeforeComplete: ToolProcessLabel
        get() = ToolProcessLabel.READING_GAME_DATA


    override suspend fun getUserDecision(): UserDecision {
        return UserDecision.Approve(label = UserApproveLabel.READ_GAME_DATA)
    }

    override suspend fun handle(
        userDecisionResult: UserDecisionResult,
        toolCallId: String,
        argumentsJson: String
    ): String {
        val isApproved = (userDecisionResult as? UserDecisionResult.Approve)?.isApproved ?: false
        return try {
            if (isApproved) {
                val simulatedChessUnits = appSettingRepository.appSettingsFlow.first()
                    .simulatedChessUnits.map { ChessPiece.from(it) }
                json.encodeToString(simulatedChessUnits)
            } else {
                createErrorResultJson(CONTENT_VALUE_REASON_REJECT_READ_DATA)
            }
        } catch (e: SerializationException) {
            createErrorResultJson(reason = e::class.simpleName.orEmpty())
        }
    }

    private fun createErrorResultJson(reason: String): String =
        json.encodeToString(mapOf(CONTENT_KEY_REASON to reason))

    override suspend fun cancel(toolCallState: ToolCallState) = Unit

    @Serializable
    internal data class ChessPiece(val team: String, val role: String, val position: String) {
        companion object {
            private const val TEAM_BLACK = "black"
            private const val TEAM_WHITE = "white"
            fun from(chessUnit: ChessUnit): ChessPiece = ChessPiece(
                team = if (chessUnit.isBlackTeam) TEAM_BLACK else TEAM_WHITE,
                role = chessUnit.unitType.lowercase(),
                position = chessUnit.positionKey
            )
        }
    }

    class Factory @Inject constructor(
        @param:AutomationDomainCommon private val json: Json,
        private val appSettingRepository: AppSettingRepository
    ) : ToolFactory {
        override val toolSignature: ToolSignature = ToolSignature(
            toolName = TOOL_NAME,
            toolDescription = """
            Reads the current state of the chess board.
            Returns an array of chess pieces, where each piece has a team, role, and position.
            Example piece: {"team": "white", "role": "king", "position": "E1"}
            
            If a user asks for advice about their game situation, read this data and give advice.
            """.trimIndent(),
            parameters = ToolParameterSignature.EMPTY
        )

        override fun createTool(): ReadCurrentChessStateTool =
            ReadCurrentChessStateTool(json, appSettingRepository)
    }

    companion object {
        const val TOOL_NAME = "read_current_chess_state"
        private const val CONTENT_KEY_REASON = "reason"
        private const val CONTENT_VALUE_REASON_REJECT_READ_DATA =
            "Read operation rejected by user. " +
                    "Please explain that we need to read data for recommendation to user."
    }
}