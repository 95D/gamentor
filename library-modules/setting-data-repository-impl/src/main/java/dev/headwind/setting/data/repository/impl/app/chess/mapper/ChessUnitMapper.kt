package dev.headwind.setting.data.repository.impl.app.chess.mapper

import dev.headwind.setting.data.source.local.app.model.chess.ChessUnitEntity
import dev.headwind.setting.model.app.chess.ChessUnit
import javax.inject.Inject

/**
 * A mapper class for mapping [ChessUnit]
 */
class ChessUnitMapper @Inject constructor() {
    fun mapToDomain(entity: ChessUnitEntity): ChessUnit = ChessUnit(
        entity.unitType,
        entity.positionKey,
        entity.isBlackTeam
    )

    fun mapToEntity(model: ChessUnit): ChessUnitEntity = ChessUnitEntity(
        model.unitType,
        model.positionKey,
        model.isBlackTeam
    )
}