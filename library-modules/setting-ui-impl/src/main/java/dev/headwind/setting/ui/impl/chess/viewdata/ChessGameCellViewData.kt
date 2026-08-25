package dev.headwind.setting.ui.impl.chess.viewdata

import androidx.compose.ui.graphics.Color

/**
 * A view data class representing chess game cell
 */
sealed interface ChessGameCellViewData {
    val cellColor: Color
    data class Empty(
        override val cellColor: Color
    ): ChessGameCellViewData

    data class WithUnit(
        override val cellColor: Color,
        val unitColor: Color,
        val positionKey: String,
        val unitType: ChessUnitType,
    ) : ChessGameCellViewData
}