package dev.headwind.setting.ui.impl.chess.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.headwind.setting.ui.impl.chess.viewdata.ChessPaletteItemViewData
import dev.headwind.setting.ui.impl.chess.viewdata.ChessUnitType

@Composable
fun ChessPaletteView(
    paletteItems: List<ChessPaletteItemViewData>,
    isBlackTeam: Boolean,
    onClickPalette: (ChessUnitType, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
        paletteItems.forEach {
            Image(
                painter = painterResource(it.unitType.icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(it.unitColor, blendMode = BlendMode.SrcIn),
                modifier = Modifier.clickable { onClickPalette(it.unitType, isBlackTeam) }
            )
        }
    }
}