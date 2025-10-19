package jp.co.nintendo.setting.ui.impl.app.compose

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingDetailContentKey
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingItemKey
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingItemViewData
import jp.co.nintendo.setting.ui.impl.common.CommonSettingDetailRow
import jp.co.nintendo.setting.ui.impl.common.CommonSettingDividerRow
import jp.co.nintendo.setting.ui.impl.common.CommonSettingSubjectRow
import jp.co.nintendo.setting.ui.impl.common.CommonSettingSwitchRow
import kotlinx.coroutines.launch

@Composable
fun AppSettingItemRow(
    viewData: AppSettingItemViewData,
    selectedContentKey: AppSettingDetailContentKey?,
    onNavigateToSettingItem: suspend (AppSettingDetailContentKey) -> Unit,
    onClickItemKey: (AppSettingItemKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    when (viewData) {
        is AppSettingItemViewData.DetailContent -> CommonSettingDetailRow(
            name = stringResource(viewData.contentKey.itemType.settingItemName),
            isSelected = viewData.contentKey == selectedContentKey,
            modifier = modifier.clickable {
                coroutineScope.launch { onNavigateToSettingItem(viewData.contentKey) }
            }
        )

        is AppSettingItemViewData.Switch -> CommonSettingSwitchRow(
            name = stringResource(viewData.itemKey.settingItemName),
            isSelected = viewData.isSelected,
            onCheckedChange = { onClickItemKey(viewData.itemKey) }
        )

        AppSettingItemViewData.Divider -> CommonSettingDividerRow()
        is AppSettingItemViewData.SubjectTitle -> CommonSettingSubjectRow(
            name = stringResource(viewData.name)
        )
    }
}