package dev.headwind.design.system.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.headwind.design.system.theme.AppTheme
import dev.headwind.design.system.theme.LocalAppSemanticColors

@Composable
fun NdsSwitch(
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalAppSemanticColors.current

    Switch(
        checked = isSelected,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = semanticColors.surfacePrimary,
            checkedTrackColor = semanticColors.settingActive,
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNdsSwitch() {
    AppTheme(isDarkTheme = false) {
        Surface {
            Column {
                NdsSwitch(
                    isSelected = false,
                    onCheckedChange = {})
                NdsSwitch(
                    isSelected = true,
                    onCheckedChange = {})
            }
        }
    }
}