package dev.headwind.setting.ui.impl.chess.viewdata

/**
 * A view data representing chess unit
 */
data class ChessUnitViewData(
    val positionKey: String,
    val unitType: ChessUnitType,
    val isBlackTeam: Boolean
)