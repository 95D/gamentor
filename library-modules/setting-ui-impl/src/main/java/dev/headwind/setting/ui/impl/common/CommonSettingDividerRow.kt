package dev.headwind.setting.ui.impl.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.headwind.design.system.theme.AppTheme
import dev.headwind.design.system.theme.LocalAppSemanticColors

@Composable
fun CommonSettingDividerRow(
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalAppSemanticColors.current
    HorizontalDivider(
        modifier = modifier
            .padding(horizontal = 6.dp)
            .padding(top = 6.dp, bottom = 16.dp),
        color = semanticColors.textSubject
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCommonSettingDividerRow() {
    AppTheme(isDarkTheme = false) {
        Surface {
            Column {
                CommonSettingDividerRow(
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}