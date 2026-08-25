package dev.headwind.setting.ui.impl.chess.viewdata

/**
 * A view data current selection of chess unit
 */
sealed interface UnitSelection {
    data object NoSelect : UnitSelection
    sealed interface Select : UnitSelection {
        data class BoardUnit(val positionKey: String) : Select
        data class PaletteUnit(val unitType: ChessUnitType, val isBlackTeam: Boolean) : Select
    }
}