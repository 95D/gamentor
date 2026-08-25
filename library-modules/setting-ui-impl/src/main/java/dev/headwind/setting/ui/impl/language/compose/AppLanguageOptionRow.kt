package dev.headwind.setting.ui.impl.language.compose

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.headwind.setting.ui.impl.common.CommonSettingOptionRow
import dev.headwind.setting.ui.impl.language.viewdata.AppLanguageOptionViewData
import dev.headwind.setting.ui.impl.language.viewdata.AppLanguageOptionViewType

@Composable
fun AppLanguageOptionRow(
    viewData: AppLanguageOptionViewData,
    onClickItem: (AppLanguageOptionViewType) -> Unit,
    modifier: Modifier = Modifier
) {
    CommonSettingOptionRow(
        name = stringResource(viewData.languageViewType.displayName),
        isSelected = viewData.isSelected,
        modifier = modifier.clickable { onClickItem(viewData.languageViewType)  }
    )
}