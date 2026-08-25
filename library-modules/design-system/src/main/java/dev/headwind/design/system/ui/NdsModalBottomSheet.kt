package dev.headwind.design.system.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.headwind.design.system.theme.AppTheme
import dev.headwind.design.system.theme.LocalAppSemanticColors

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NdsModalBottomSheet(
    isVisible: Boolean,
    onDismissBottomSheet: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(isVisible) {
        when {
            isVisible -> sheetState.show()
            sheetState.isVisible -> sheetState.hide()
        }
    }

    val semanticColors = LocalAppSemanticColors.current
    val windowInsets = WindowInsets.systemBars
        .union(WindowInsets.displayCutout)
        .union(WindowInsets.ime)
    if (isVisible || sheetState.isVisible) {
        ModalBottomSheet(
            modifier = modifier
                .windowInsetsPadding(
                    windowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                ),
            onDismissRequest = onDismissBottomSheet,
            dragHandle = {},
            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
            containerColor = semanticColors.surfacePrimary,
            content = content,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNdsBottomSheet() {
    AppTheme(isDarkTheme = false) {
        val semanticColors = LocalAppSemanticColors.current
        NdsModalBottomSheet(
            isVisible = true,
            onDismissBottomSheet = { /* Do nothing */ },
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .width(IntrinsicSize.Max)
            ) {
                Text(
                    text = "Snack bar test",
                    fontWeight = FontWeight.Bold,
                    color = semanticColors.textDefault,
                    modifier = Modifier
                )
            }
        }
    }
}
