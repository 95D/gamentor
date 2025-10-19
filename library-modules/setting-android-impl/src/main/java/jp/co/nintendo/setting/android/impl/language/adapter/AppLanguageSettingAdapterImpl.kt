package jp.co.nintendo.setting.android.impl.language.adapter

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import jp.co.nintendo.setting.language.adapter.AppLanguageSettingAdapter
import javax.inject.Inject

/**
 * An implementation of [AppLanguageSettingAdapter]
 */
class AppLanguageSettingAdapterImpl @Inject constructor() : AppLanguageSettingAdapter {
    override fun getCurrentLanguageTag(): String {
        return AppCompatDelegate.getApplicationLocales()
            .toLanguageTags()
            .split(",").firstOrNull() ?: ""
    }

    override fun setAppLanguageTag(languageTag: String) {
        val localeList = if (languageTag.isEmpty()) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageTag)
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}