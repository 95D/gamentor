package dev.headwind.setting.ui.impl.language.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import dev.headwind.setting.language.adapter.AppLanguageSettingAdapter
import dev.headwind.setting.ui.impl.language.viewdata.AppLanguageOptionViewData
import dev.headwind.setting.ui.impl.language.viewdata.AppLanguageOptionViewType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * A view model class for managing state in app language setting screen
 */
@HiltViewModel
class AppLanguageSettingViewModel @Inject constructor(
    private val appLanguageSettingAdapter: AppLanguageSettingAdapter
) : ViewModel() {
    private val currentLanguageMutableStateFlow: MutableStateFlow<AppLanguageOptionViewType> =
        MutableStateFlow(getCurrentAppLanguageType())

    val appLanguagesStateFlow: StateFlow<List<AppLanguageOptionViewData>> =
        currentLanguageMutableStateFlow.map { selectedLanguage ->
            AppLanguageOptionViewType.entries.map {
                AppLanguageOptionViewData(
                    isSelected = it == selectedLanguage,
                    languageViewType = it
                )
            }.toList()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun selectAppLanguage(appLanguageOptionViewType: AppLanguageOptionViewType) {
        appLanguageSettingAdapter.setAppLanguageTag(appLanguageOptionViewType.languageTag)
        currentLanguageMutableStateFlow.value = getCurrentAppLanguageType()
    }

    private fun getCurrentAppLanguageType(): AppLanguageOptionViewType =
        AppLanguageOptionViewType.from(appLanguageSettingAdapter.getCurrentLanguageTag())
}