package dev.headwind.setting.data.source.local.app.model.chess

import kotlinx.serialization.Serializable

/**
 * A data model representing one unit of chess game simulation
 */
@Serializable
class ChessUnitEntity(
    val unitType: String,
    val positionKey: String,
    val isBlackTeam: Boolean
)