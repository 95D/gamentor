package dev.headwind.setting.ui.impl.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.headwind.design.system.theme.AppTheme
import dev.headwind.design.system.theme.LocalAppSemanticColors

@Composable
fun CommonSettingOptionRow(
    name: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val semanticColors = LocalAppSemanticColors.current
    val textColor = if (isSelected) {
        semanticColors.settingActive
    } else {
        semanticColors.textDefault
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            color = textColor
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommonSettingOptionRow() {
    AppTheme(isDarkTheme = false) {
        Surface {
            Column {
                CommonSettingOptionRow(name = "Test item 1", isSelected = false)
                CommonSettingOptionRow(name = "Test item 2", isSelected = true)
            }
        }
    }
}