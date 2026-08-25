package dev.headwind.setting.ui.impl.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.headwind.setting.data.repository.app.AppSettingRepository
import dev.headwind.setting.ui.impl.theme.viewdata.AppThemeOptionViewData
import dev.headwind.setting.ui.impl.theme.viewdata.AppThemeOptionViewType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A view model class for managing state in app theme setting screen
 */
@HiltViewModel
class AppThemeSettingViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository
) : ViewModel() {
    val appThemesStateFlow: StateFlow<List<AppThemeOptionViewData>> = appSettingRepository
        .appSettingsFlow
        .map { appSettings ->
            val selectedTheme = appSettings.appliedThemeType
            AppThemeOptionViewType.entries.map { viewType ->
                AppThemeOptionViewData(
                    isSelected = selectedTheme == viewType.themeType,
                    themeViewType = viewType
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun selectTheme(themeOptionViewType: AppThemeOptionViewType) {
        viewModelScope.launch {
            appSettingRepository.updateApplyThemeType(themeOptionViewType.themeType)
        }
    }
}