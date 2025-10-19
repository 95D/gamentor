package jp.co.nintendo.design.system.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import jp.co.nintendo.design.system.animation.ndsNoneTransform
import jp.co.nintendo.design.system.animation.ndsTargetFadeInContentTransform
import jp.co.nintendo.design.system.theme.AppTheme
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors
import jp.co.nintendo.multi.lang.resources.R as MultiLangR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NdsListScreenAppBar(
    isExpandedScreen: Boolean,
    title: String,
    onBackClicked: () -> Unit,
    isDetailSelected: Boolean,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val semanticColors = LocalAppSemanticColors.current
    val titleTransitionSpec = remember(isExpandedScreen) {
        getTitleTransitionSpec(isExpandedScreen)
    }

    AnimatedContent(
        targetState = title,
        transitionSpec = { titleTransitionSpec },
        label = "Title change"
    ) { targetTitle ->
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = semanticColors.header,
                titleContentColor = semanticColors.headerText
            ),
            title = { Text(targetTitle) },
            actions = actions,
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
}

private fun getTitleTransitionSpec(isExpandedScreen: Boolean): ContentTransform =
    if (isExpandedScreen) {
        ndsTargetFadeInContentTransform
    } else {
        ndsNoneTransform
    }

@Preview(showBackground = true)
@Composable
fun PreviewNdsListScreenAppBar() {
    AppTheme(isDarkTheme = false) {
        NdsListScreenAppBar(
            isExpandedScreen = false,
            title = "Test app bar",
            onBackClicked = { /* Do nothing */ },
            isDetailSelected = false
        )
    }
}
