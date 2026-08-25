package dev.headwind.setting.ui.impl.language.viewdata

import androidx.annotation.StringRes
import java.util.Locale
import dev.headwind.multi.lang.resources.R as MultiLangR

/**
 * An enum class representing view type of App language option
 */
enum class AppLanguageOptionViewType(
    @param:StringRes val displayName: Int,
    val languageTag: String
) {
    DEFAULT(displayName = MultiLangR.string.locale_default, languageTag = ""),
    ENGLISH(displayName = MultiLangR.string.locale_english, languageTag = Locale.ENGLISH.language),
    JAPANESE(
        displayName = MultiLangR.string.locale_japanese,
        languageTag = Locale.JAPANESE.language
    );

    companion object {
        fun from(languageTag: String): AppLanguageOptionViewType = entries.firstOrNull {
            it.languageTag == languageTag
        } ?: DEFAULT
    }
}