package jp.co.nintendo.automation.usecase.impl.tool.actual

import jp.co.nintendo.automation.domain.tool.model.ToolParameterSignature
import jp.co.nintendo.automation.domain.tool.model.ToolProcessLabel
import jp.co.nintendo.automation.domain.tool.model.ToolSignature
import jp.co.nintendo.automation.domain.tool.model.decision.UserApproveLabel
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecision
import jp.co.nintendo.automation.domain.tool.model.decision.UserDecisionResult
import jp.co.nintendo.automation.usecase.impl.di.AutomationDomainCommon
import jp.co.nintendo.automation.usecase.impl.tool.Tool
import jp.co.nintendo.automation.usecase.impl.tool.ToolFactory
import jp.co.nintendo.automation.usecase.impl.tool.model.ToolCallState
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ReadCurrentChessStateTool(private val json: Json) : Tool {
    override val labelBeforeStart: ToolProcessLabel
        get() = ToolProcessLabel.PREPARING_TOOL

    override val labelBeforeDecide: ToolProcessLabel
        get() = ToolProcessLabel.REQUESTING_APPROVE

    override val labelBeforeComplete: ToolProcessLabel
        get() = ToolProcessLabel.READING_GAME_DATA


    // TODO: Please migrate real chess simulation
    private val chessState = listOf(
        ChessPiece(team = "white", role = "king", position = "C1"),
        ChessPiece(team = "white", role = "queen", position = "D2"),
        ChessPiece(team = "white", role = "rook", position = "D1"),
        ChessPiece(team = "white", role = "rook", position = "H1"),
        ChessPiece(team = "white", role = "bishop", position = "G5"),
        ChessPiece(team = "white", role = "bishop", position = "E3"),
        ChessPiece(team = "white", role = "knight", position = "F3"),
        ChessPiece(team = "white", role = "knight", position = "C4"),

        ChessPiece(team = "white", role = "pawn", position = "A2"),
        ChessPiece(team = "white", role = "pawn", position = "B2"),
        ChessPiece(team = "white", role = "pawn", position = "D4"),
        ChessPiece(team = "white", role = "pawn", position = "F2"),
        ChessPiece(team = "white", role = "pawn", position = "G2"),
        ChessPiece(team = "white", role = "pawn", position = "H2"),

        ChessPiece(team = "black", role = "king", position = "G8"),
        ChessPiece(team = "black", role = "queen", position = "A5"),
        ChessPiece(team = "black", role = "rook", position = "A8"),
        ChessPiece(team = "black", role = "rook", position = "F8"),
        ChessPiece(team = "black", role = "bishop", position = "E7"),
        ChessPiece(team = "black", role = "bishop", position = "G7"),
        ChessPiece(team = "black", role = "knight", position = "C6"),
        ChessPiece(team = "black", role = "knight", position = "D7"),

        ChessPiece(team = "black", role = "pawn", position = "C5"),
        ChessPiece(team = "black", role = "pawn", position = "D6"),
        ChessPiece(team = "black", role = "pawn", position = "E6"),
        ChessPiece(team = "black", role = "pawn", position = "F7"),
        ChessPiece(team = "black", role = "pawn", position = "H7"),
        ChessPiece(team = "black", role = "pawn", position = "B7")
    )

    override suspend fun getUserDecision(): UserDecision {
        return UserDecision.Approve(label = UserApproveLabel.READ_GAME_DATA)
    }

    override suspend fun handle(
        userDecisionResult: UserDecisionResult,
        toolCallId: String,
        argumentsJson: String
    ): String {

        return try {
            json.encodeToString(chessState)
        } catch (e: SerializationException) {
            createErrorResultJson(reason = e::class.simpleName.orEmpty())
        }
    }

    private fun createErrorResultJson(reason: String): String =
        json.encodeToString(mapOf(CONTENT_KEY_REASON to reason))

    override suspend fun cancel(toolCallState: ToolCallState) = Unit

    @Serializable
    data class ChessPiece(val team: String, val role: String, val position: String)

    class Factory @Inject constructor(
        @param:AutomationDomainCommon private val json: Json
    ) : ToolFactory {
        override val toolSignature: ToolSignature = ToolSignature(
            toolName = TOOL_NAME,
            toolDescription = """
            Reads the current state of the chess board.
            Returns an array of chess pieces, where each piece has a team, role, and position.
            Example piece: {"team": "white", "role": "king", "position": "E1"}
            
            Please only invoke it by the command '/read_chess_state'.
            """.trimIndent(),
            parameters = ToolParameterSignature.EMPTY
        )

        override fun createTool(): ReadCurrentChessStateTool = ReadCurrentChessStateTool(json)
    }

    companion object {
        const val TOOL_NAME = "read_current_chess_state"
        private const val CONTENT_KEY_REASON = "reason"
    }
}