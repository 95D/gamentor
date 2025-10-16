package jp.co.nintendo.design.system.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import jp.co.nintendo.design.system.theme.AppTheme
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors
import jp.co.nintendo.multi.lang.resources.R as MultiLangR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NdsDetailScreenAppBar(
    isExpandedScreen: Boolean,
    title: String,
    onBackClicked: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val semanticColors = LocalAppSemanticColors.current
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
        containerColor = semanticColors.header, titleContentColor = semanticColors.headerText
    ), title = {
        if (!isExpandedScreen) {
            Text(title)
        }
    }, navigationIcon = {
        if (!isExpandedScreen) {
            IconButton(onClick = onBackClicked) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(MultiLangR.string.content_description_back_action)
                )
            }
        }
    }, actions = actions
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNdsDetailScreenAppBar() {
    AppTheme(isDarkTheme = false) {
        NdsDetailScreenAppBar(
            isExpandedScreen = false,
            title = "Test app bar",
            onBackClicked = { /* Do nothing */ },
        )
    }
}
