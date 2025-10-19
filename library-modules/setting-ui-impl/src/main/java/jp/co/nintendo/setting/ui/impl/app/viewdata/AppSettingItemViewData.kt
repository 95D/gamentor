package jp.co.nintendo.setting.ui.impl.app.viewdata

import androidx.annotation.StringRes

/**
 * A view data class representing one row of setting list item
 */
sealed interface AppSettingItemViewData {
    data class SubjectTitle(@param:StringRes val name: Int) : AppSettingItemViewData
    data class DetailContent(val contentKey: AppSettingDetailContentKey) : AppSettingItemViewData
    data class Switch(
        val itemKey: AppSettingItemKey,
        val isSelected: Boolean
    ) : AppSettingItemViewData

    data object Divider : AppSettingItemViewData
}