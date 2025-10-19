package jp.co.nintendo.setting.ui.impl.app.viewdata

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import jp.co.nintendo.multi.lang.resources.R as MultiLangR

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