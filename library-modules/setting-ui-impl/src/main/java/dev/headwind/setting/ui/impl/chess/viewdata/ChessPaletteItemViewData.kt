package dev.headwind.setting.ui.impl.chess.viewdata

import androidx.compose.ui.graphics.Color

/**
 * A view data representing palette item of editing chess
 */
data class ChessPaletteItemViewData(
    val unitColor: Color,
    val unitType: ChessUnitType,
    val isMoreSelectable: Boolean
)