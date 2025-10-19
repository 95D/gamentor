package jp.co.nintendo.setting.ui.impl.theme.viewdata

import androidx.annotation.StringRes
import jp.co.nintendo.setting.model.app.theme.AppThemeType
import jp.co.nintendo.multi.lang.resources.R as MultiLangR

/**
 * An enum class representing view type of App theme option
 */
enum class AppThemeOptionViewType(
    @param:StringRes val displayName: Int,
    val themeType: AppThemeType
) {
    DEVICE(displayName = MultiLangR.string.theme_device, themeType = AppThemeType.DEVICE),
    LIGHT(displayName = MultiLangR.string.theme_light, themeType = AppThemeType.LIGHT),
    DARK(displayName = MultiLangR.string.theme_dark, themeType = AppThemeType.DARK),
    SAKURA(displayName = MultiLangR.string.theme_sakura, themeType = AppThemeType.SAKURA);
}