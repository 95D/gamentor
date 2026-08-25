package dev.headwind.setting.ui.impl.app.viewdata

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import dev.headwind.multi.lang.resources.R as MultiLangR

/**
 * An enum class representing app setting item
 */
@Parcelize
enum class AppSettingDetailItemType(
    @param:StringRes val settingItemName: Int
) : Parcelable {
    APP_LANGUAGE(settingItemName = MultiLangR.string.setting_item_app_language),
    APP_THEME(settingItemName = MultiLangR.string.setting_item_app_theme),
    CHESS_EDIT(settingItemName = MultiLangR.string.setting_item_edit_chess)
}