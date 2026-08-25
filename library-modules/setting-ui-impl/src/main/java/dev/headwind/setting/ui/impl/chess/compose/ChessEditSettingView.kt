package dev.headwind.setting.ui.impl.chess.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.headwind.setting.ui.impl.chess.viewdata.ChessGameNotationType
import dev.headwind.setting.ui.impl.chess.viewmodel.ChessGameViewModel
import dev.headwind.multi.lang.resources.R as MultiLangR

@Composable
fun ChessEditSettingView(
    modifier: Modifier = Modifier,
    chessGameViewModel: ChessGameViewModel = hiltViewModel()
) {
    val gameViewData by chessGameViewModel.gameViewDataStateFlow.collectAsState()
    Column(modifier = modifier) {
        ChessGridView(gameViewData.gridViewData, onClickCell = chessGameViewModel::clickCell)
        ChessPaletteView(
            paletteItems = gameViewData.whiteItems,
            isBlackTeam = false,
            onClickPalette = chessGameViewModel::clickPalette
        )
        ChessPaletteView(
            paletteItems = gameViewData.blackItems,
            isBlackTeam = true,
            onClickPalette = chessGameViewModel::clickPalette
        )
        ChessResetButton(
            onClick = { chessGameViewModel.resetGrid(ChessGameNotationType.EMPTY) },
            text = stringResource(MultiLangR.string.chess_reset_empty)
        )
        ChessResetButton(
            onClick = { chessGameViewModel.resetGrid(ChessGameNotationType.BASE) },
            text = stringResource(MultiLangR.string.chess_reset_standard)
        )
        ChessResetButton(
            onClick = { chessGameViewModel.resetGrid(ChessGameNotationType.FOOL_MATES) },
            text = stringResource(MultiLangR.string.chess_reset_fools_mate)
        )
    }
}