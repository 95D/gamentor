package dev.headwind.setting.ui.impl.chess.viewmodel.factory

import jakarta.inject.Inject
import dev.headwind.setting.ui.impl.chess.viewdata.ChessGameNotationType
import dev.headwind.setting.ui.impl.chess.viewdata.ChessUnitType
import dev.headwind.setting.ui.impl.chess.viewdata.ChessUnitViewData

/**
 * A factory class for creating chess grid according to notation type
 */
class ChessGridFactory @Inject constructor() {
    fun createGridViewData(notationType: ChessGameNotationType): Map<String, ChessUnitViewData> =
        when(notationType) {
            ChessGameNotationType.EMPTY -> emptyMap()
            ChessGameNotationType.BASE -> generateStandardGrid()
            ChessGameNotationType.FOOL_MATES -> generateFoolsMateGrid()
        }

    private fun createStandardPieces(
        isBlackTeam: Boolean,
        pieceRank: Int,
        pawnRank: Int
    ): Map<String, ChessUnitViewData> {
        val pieces = mutableMapOf<String, ChessUnitViewData>()

        val pieceOrder = listOf(
            ChessUnitType.ROOK, ChessUnitType.KNIGHT, ChessUnitType.BISHOP,
            ChessUnitType.QUEEN, ChessUnitType.KING,
            ChessUnitType.BISHOP, ChessUnitType.KNIGHT, ChessUnitType.ROOK
        )

        pieceOrder.forEachIndexed { fileIndex, unitType ->
            val fileChar = 'A' + fileIndex
            val positionKey = "$fileChar$pieceRank"
            pieces[positionKey] = ChessUnitViewData(
                positionKey = positionKey,
                unitType = unitType,
                isBlackTeam = isBlackTeam
            )
        }

        (0 until 8).forEach { fileIndex ->
            val fileChar = 'A' + fileIndex
            val positionKey = "$fileChar$pawnRank"
            pieces[positionKey] = ChessUnitViewData(
                positionKey = positionKey,
                unitType = ChessUnitType.PAWN,
                isBlackTeam = isBlackTeam
            )
        }

        return pieces
    }

    fun generateStandardGrid(): Map<String, ChessUnitViewData> {
        val whitePieces = createStandardPieces(isBlackTeam = false, pieceRank = 1, pawnRank = 2)
        val blackPieces = createStandardPieces(isBlackTeam = true, pieceRank = 8, pawnRank = 7)
        return whitePieces + blackPieces
    }

    fun generateFoolsMateGrid(): Map<String, ChessUnitViewData> {
        val standardBoard = generateStandardGrid().toMutableMap()

        standardBoard.remove("F2")
        standardBoard["F3"] = ChessUnitViewData("F3", ChessUnitType.PAWN, isBlackTeam = false)

        standardBoard.remove("G2")
        standardBoard["G4"] = ChessUnitViewData("G4", ChessUnitType.PAWN, isBlackTeam = false)

        standardBoard.remove("E7")
        standardBoard["E6"] = ChessUnitViewData("E6", ChessUnitType.PAWN, isBlackTeam = true)

        standardBoard.remove("D8")
        standardBoard["H4"] = ChessUnitViewData("H4", ChessUnitType.QUEEN, isBlackTeam = true)

        return standardBoard.toMap()
    }
}