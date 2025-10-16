package jp.co.nintendo.design.system.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.nintendo.design.system.theme.AppTheme
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors
import jp.co.nintendo.multi.lang.resources.R as MultiLangR

@Composable
fun NdsExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val semanticColors = LocalAppSemanticColors.current
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    val icon = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
    val contentDescription = if (isExpanded) {
        MultiLangR.string.content_description_fold
    } else {
        MultiLangR.string.content_description_unfold
    }

    Column(modifier = modifier.clickable {
        isExpanded = !isExpanded
    }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = semanticColors.textDefault,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = icon,
                contentDescription = stringResource(contentDescription)
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNdsExpandableText() {
    AppTheme(isDarkTheme = false) {
        val semanticColors = LocalAppSemanticColors.current
        NdsExpandableText(text = "Test expandable text") {
            Text(
                text = "Snack bar test",
                fontWeight = FontWeight.Bold,
                color = semanticColors.textDefault,
                modifier = Modifier
            )
        }
    }
}
