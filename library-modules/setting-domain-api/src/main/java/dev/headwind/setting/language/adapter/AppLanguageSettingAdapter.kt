package dev.headwind.setting.language.adapter

/**
 * An use case for setting of app language
 */
interface AppLanguageSettingAdapter {
    fun getCurrentLanguageTag(): String
    fun setAppLanguageTag(languageTag: String)
}