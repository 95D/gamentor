package dev.headwind.setting.ui.impl.chess.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.headwind.setting.ui.impl.chess.viewdata.ChessGameCellViewData


@Composable
fun ChessGridView(
    gridViewData: List<List<ChessGameCellViewData>>,
    onClickCell: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp)
            .border(2.dp, Color.Black)
    ) {
        gridViewData.forEachIndexed { row, lineViewData ->
            Row(modifier = Modifier.weight(1f)) {
                lineViewData.forEachIndexed { col, item ->
                    ChessCell(
                        cellData = item,
                        onClickCell = { onClickCell(row, col) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ChessCell(
    cellData: ChessGameCellViewData,
    onClickCell: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(cellData.cellColor)
            .clickable(onClick = onClickCell),
        contentAlignment = Alignment.Center
    ) {
        if (cellData is ChessGameCellViewData.WithUnit) {
            Image(
                painter = painterResource(cellData.unitType.icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(cellData.unitColor, blendMode = BlendMode.SrcIn)
            )
        }
    }
}