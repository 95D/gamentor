package jp.co.nintendo.setting.ui.impl.chess.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors

@Composable
fun ChessResetButton(
    onClick: () -> Unit,
    text: String
) {
    val semanticColors = LocalAppSemanticColors.current
    Text(
        text,
        color = semanticColors.textDefault,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 6.dp)
            .clickable { onClick() })
}