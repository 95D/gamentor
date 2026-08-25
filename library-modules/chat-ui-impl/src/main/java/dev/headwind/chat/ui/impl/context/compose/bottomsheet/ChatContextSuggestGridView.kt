package dev.headwind.chat.ui.impl.context.compose.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.headwind.chat.ui.impl.context.viewdata.ChatContextActionType
import dev.headwind.design.system.theme.LocalAppSemanticColors

/**
 * A grid content view for suggestion context actions of the content
 */
@Composable
fun ChatContextSuggestGridView(
    contextActions: List<ChatContextActionType>,
    onSelectMessageContextAction: (ChatContextActionType) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp, max = 300.dp)
    ) {
        items(contextActions.size) { index ->
            GridActionTile(
                actionViewData = contextActions[index],
                onClick = onSelectMessageContextAction
            )
        }
    }
}

@Composable
private fun GridActionTile(
    actionViewData: ChatContextActionType,
    onClick: (ChatContextActionType) -> Unit
) {
    val semanticColors = LocalAppSemanticColors.current
    Column(
        modifier = Modifier
            .size(100.dp)
            .clickable { onClick(actionViewData) }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val actionName = stringResource(actionViewData.actionName)
        Icon(
            imageVector = getIcon(actionViewData),
            contentDescription = actionName,
            modifier = Modifier.size(28.dp),
            tint = semanticColors.textDefault
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = actionName,
            color = semanticColors.textDefault,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

private fun getIcon(contextActionViewData: ChatContextActionType): ImageVector =
    when (contextActionViewData) {
        ChatContextActionType.DELETE -> Icons.Default.Delete
    }