package jp.co.nintendo.setting.ui.impl.chess.viewmodel.mapper

import jp.co.nintendo.setting.model.app.chess.ChessUnit
import jp.co.nintendo.setting.ui.impl.chess.viewdata.ChessUnitType
import jp.co.nintendo.setting.ui.impl.chess.viewdata.ChessUnitViewData
import javax.inject.Inject

/**
 * A mapper class for mapping [ChessUnit] and [ChessUnitViewData]
 */
class ChessUnitMapper @Inject constructor() {
    fun mapToViewData(chessUnit: ChessUnit): ChessUnitViewData? =
        ChessUnitType.entries.firstOrNull { it.name == chessUnit.unitType }?.let {
            ChessUnitViewData(
                positionKey = chessUnit.positionKey,
                unitType = it,
                isBlackTeam = chessUnit.isBlackTeam
            )
        }

    fun mapToDomainModel(viewData: ChessUnitViewData): ChessUnit =
        ChessUnit(
            positionKey = viewData.positionKey,
            unitType = viewData.unitType.name,
            isBlackTeam = viewData.isBlackTeam
        )
}