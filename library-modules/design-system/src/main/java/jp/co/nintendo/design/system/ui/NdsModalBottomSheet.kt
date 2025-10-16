package jp.co.nintendo.design.system.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.co.nintendo.design.system.theme.LocalAppSemanticColors

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