package jp.co.nintendo.design.system.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors

@Composable
fun NdsSnackBar(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomCenter,
    content: @Composable () -> Unit
) {
    val semanticColors = LocalAppSemanticColors.current
    Box(
        modifier = modifier.fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(corner = CornerSize(6.dp)))
            .background(color = semanticColors.surfaceSecondary)
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight }
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight }
            ) + fadeOut(),
            modifier = Modifier.align(alignment)
        ) {
            content()
        }
    }
}