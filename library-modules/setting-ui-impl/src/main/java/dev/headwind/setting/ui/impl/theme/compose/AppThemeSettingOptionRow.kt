package dev.headwind.setting.ui.impl.theme.compose

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.headwind.setting.ui.impl.common.CommonSettingOptionRow
import dev.headwind.setting.ui.impl.theme.viewdata.AppThemeOptionViewData
import dev.headwind.setting.ui.impl.theme.viewdata.AppThemeOptionViewType

@Composable
fun AppThemeSettingOptionRow(
    viewData: AppThemeOptionViewData,
    onClickItem: (AppThemeOptionViewType) -> Unit,
    modifier: Modifier = Modifier
) {
    CommonSettingOptionRow(
        name = stringResource(viewData.themeViewType.displayName),
        isSelected = viewData.isSelected,
        modifier = modifier.clickable { onClickItem(viewData.themeViewType)  }
    )
}