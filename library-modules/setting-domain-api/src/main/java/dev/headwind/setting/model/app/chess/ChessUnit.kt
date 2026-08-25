package dev.headwind.setting.model.app.chess

/**
 * A setting domain model for editing chess simulation data
 */
data class ChessUnit(
    val unitType: String,
    val positionKey: String,
    val isBlackTeam: Boolean
)