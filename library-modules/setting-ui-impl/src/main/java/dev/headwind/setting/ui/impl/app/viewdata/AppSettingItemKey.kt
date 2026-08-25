package dev.headwind.setting.ui.impl.app.viewdata

import androidx.annotation.StringRes
import dev.headwind.multi.lang.resources.R as MultiLangR

/**
 * An enum class representing for directly showing items under app setting list view
 */
enum class AppSettingItemKey(
    @param:StringRes val settingItemName: Int
) {
    SHOW_ALL_MESSAGES(settingItemName = MultiLangR.string.setting_item_show_all_messages)
}