package dev.headwind.setting.ui.impl.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.headwind.design.system.theme.AppTheme
import dev.headwind.design.system.theme.LocalAppSemanticColors
import dev.headwind.design.system.ui.NdsSwitch

@Composable
fun CommonSettingSwitchRow(
    name: String,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalAppSemanticColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isSelected) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            color = semanticColors.textDefault
        )

        NdsSwitch(
            isSelected = isSelected,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommonSettingSwitchRow() {
    AppTheme(isDarkTheme = false) {
        Surface {
            Column {
                CommonSettingSwitchRow(
                    name = "Test item 1",
                    isSelected = false,
                    onCheckedChange = {})
                CommonSettingSwitchRow(
                    name = "Test item 2",
                    isSelected = true,
                    onCheckedChange = {})
            }
        }
    }
}