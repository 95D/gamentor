package jp.co.nintendo.design.system.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
fun NdsListScreenAppBar(
    title: String,
    onBackClicked: () -> Unit,
    isDetailSelected: Boolean
) {
    val semanticColors = LocalAppSemanticColors.current
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = semanticColors.header,
            titleContentColor = semanticColors.headerText
        ),
        title = {
            AnimatedContent(
                targetState = title,
                transitionSpec = {
                    (slideInHorizontally { fullWidth -> fullWidth } + fadeIn())
                        .togetherWith(slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut())
                },
                label = "Title change"
            ) { targetTitle ->
                Text(targetTitle)
            }
        },
        navigationIcon = {
            AnimatedVisibility(
                visible = isDetailSelected,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription =
                            stringResource(MultiLangR.string.content_description_back_action)
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNdsListScreenAppBar() {
    AppTheme(isDarkTheme = false) {
        NdsListScreenAppBar(
            title = "Test app bar",
            onBackClicked = { /* Do nothing */ },
            isDetailSelected = false
        )
    }
}
