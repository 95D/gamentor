package jp.co.nintendo.setting.ui.impl.chess.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import jp.co.nintendo.setting.data.repository.app.AppSettingRepository
import jp.co.nintendo.setting.ui.impl.chess.color.ChessColors
import jp.co.nintendo.setting.ui.impl.chess.viewdata.ChessGameCellViewData
import jp.co.nintendo.setting.ui.impl.chess.viewdata.ChessGameNotationType
import jp.co.nintendo.setting.ui.impl.chess.viewdata.ChessGameViewData
import jp.co.nintendo.setting.ui.impl.chess.viewdata.ChessPaletteItemViewData
import jp.co.nintendo.setting.ui.impl.chess.viewdata.ChessUnitType
import jp.co.nintendo.setting.ui.impl.chess.viewdata.ChessUnitViewData
import jp.co.nintendo.setting.ui.impl.chess.viewdata.UnitSelection
import jp.co.nintendo.setting.ui.impl.chess.viewmodel.factory.ChessGridFactory
import jp.co.nintendo.setting.ui.impl.chess.viewmodel.mapper.ChessUnitMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * A view model class for chess game screen
 */
@HiltViewModel
class ChessGameViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository,
    private val chessUnitMapper: ChessUnitMapper,
    private val chessGridFactory: ChessGridFactory
) : ViewModel() {
    private val unitsMutableStateFlow: StateFlow<Map<String, ChessUnitViewData>> =
        appSettingRepository.appSettingsFlow.map {
            it.simulatedChessUnits
                .mapNotNull(chessUnitMapper::mapToViewData)
                .associateBy { unit -> unit.positionKey }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )
    private val unitSelectionMutableStateFlow: MutableStateFlow<UnitSelection> =
        MutableStateFlow(UnitSelection.NoSelect)

    val gameViewDataStateFlow: StateFlow<ChessGameViewData> = combine(
        unitsMutableStateFlow,
        unitSelectionMutableStateFlow
    ) {
        ChessGameViewData(
            blackItems = getPaletteItems(isBlackTeam = true),
            whiteItems = getPaletteItems(isBlackTeam = false),
            gridViewData = getGridViewData()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ChessGameViewData(
            blackItems = getPaletteItems(isBlackTeam = true),
            whiteItems = getPaletteItems(isBlackTeam = false),
            gridViewData = getGridViewData()
        )
    )

    private fun getPaletteItems(isBlackTeam: Boolean): List<ChessPaletteItemViewData> =
        ChessUnitType.entries.map {
            ChessPaletteItemViewData(
                unitColor = getUnitColor(isBlackTeam),
                unitType = it,
                isMoreSelectable = isSelectableChessPalette(
                    unitType = it,
                    isBlackTeam = isBlackTeam
                )
            )
        }

    private fun getGridViewData(): List<List<ChessGameCellViewData>> =
        (0 until 8).map { row ->
            (0 until 8).map { col ->
                getGameCellViewData(row, col)
            }
        }

    private fun isSelectableChessPalette(unitType: ChessUnitType, isBlackTeam: Boolean): Boolean =
        unitsMutableStateFlow.value.values.count {
            it.isBlackTeam == isBlackTeam && it.unitType == unitType
        } < unitType.maximumCount

    private fun getGameCellViewData(row: Int, col: Int): ChessGameCellViewData {
        val positionKey = getPositionKey(col, row)
        val unit = unitsMutableStateFlow.value[positionKey]
        return if (unit == null) {
            ChessGameCellViewData.Empty(
                cellColor = getCellColor(col = col, row = row, isSelected = false)
            )
        } else {
            val isSelected =
                (unitSelectionMutableStateFlow.value as? UnitSelection.Select.BoardUnit)
                    ?.positionKey == positionKey
            ChessGameCellViewData.WithUnit(
                cellColor = getCellColor(col = col, row = row, isSelected = isSelected),
                unitColor = getUnitColor(unit.isBlackTeam),
                positionKey = unit.positionKey,
                unitType = unit.unitType
            )
        }
    }

    fun clickCell(row: Int, col: Int) {
        val positionKey = getPositionKey(col, row)
        val units = unitsMutableStateFlow.value
        val selectedUnit = unitSelectionMutableStateFlow.value
        val destinationUnit = units[positionKey]
        if (selectedUnit is UnitSelection.Select) {
            mayCommitUnit(
                destPositionKey = positionKey,
                unitSelection = selectedUnit,
                units = units
            )
        } else {
            destinationUnit?.let { maySelectUnit(positionKey, units) }
        }
    }

    private fun mayCommitUnit(
        destPositionKey: String,
        unitSelection: UnitSelection.Select,
        units: Map<String, ChessUnitViewData>
    ) {
        val sourceUnit = when (unitSelection) {
            is UnitSelection.Select.BoardUnit -> units[unitSelection.positionKey]
            is UnitSelection.Select.PaletteUnit -> ChessUnitViewData(
                positionKey = destPositionKey,
                unitType = unitSelection.unitType,
                isBlackTeam = unitSelection.isBlackTeam
            )
        }

        if (sourceUnit != null) {
            val destUnit = sourceUnit.copy(positionKey = destPositionKey)
            val nextUnits = units - sourceUnit.positionKey + (destUnit.positionKey to destUnit)
            viewModelScope.launch {
                appSettingRepository.updateChessSimulation(
                    nextUnits.values.map(chessUnitMapper::mapToDomainModel)
                )
            }

        }
        unitSelectionMutableStateFlow.value = UnitSelection.NoSelect
    }

    private fun maySelectUnit(
        positionKey: String,
        units: Map<String, ChessUnitViewData>
    ) {
        val unit = units[positionKey] ?: return
        unitSelectionMutableStateFlow.value = UnitSelection.Select.BoardUnit(
            unit.positionKey
        )
    }

    fun clickPalette(unitType: ChessUnitType, isBlackTeam: Boolean) {
        val isSelectable = isSelectableChessPalette(unitType, isBlackTeam)
        if (isSelectable) {
            unitSelectionMutableStateFlow.value = UnitSelection.Select.PaletteUnit(
                unitType,
                isBlackTeam
            )
        }
    }

    fun resetGrid(notationType: ChessGameNotationType) {
        val units = chessGridFactory.createGridViewData(notationType)
        viewModelScope.launch {
            appSettingRepository.updateChessSimulation(
                units.values.map(chessUnitMapper::mapToDomainModel)
            )
        }
    }

    private fun getPositionKey(row: Int, col: Int): String {
        val fileChar = 'A' + col
        val rankNumber = 8 - row
        return "$fileChar$rankNumber"
    }

    private fun isBlackCell(row: Int, col: Int): Boolean {
        return (col + row) % 2 == 1
    }

    private fun getCellColor(row: Int, col: Int, isSelected: Boolean): Color = when {
        isSelected -> ChessColors.CellColorSelected
        isBlackCell(col = col, row = row) -> ChessColors.CellColorBlack
        else -> ChessColors.CellColorWhite
    }

    private fun getUnitColor(isBlackTeam: Boolean): Color = if (isBlackTeam) {
        ChessColors.UnitColorBlack
    } else {
        ChessColors.UnitColorWhite
    }
}