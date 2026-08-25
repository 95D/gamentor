package dev.headwind.gamentor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.headwind.setting.data.repository.app.AppSettingRepository
import dev.headwind.setting.model.app.theme.AppThemeType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * A view model class for managing UI states whose changes are propagated to all states of the App
 */
@HiltViewModel
class AppConfigurationViewModel @Inject constructor(
    appSettingRepository: AppSettingRepository
) : ViewModel() {
    val themeTypeFlow: StateFlow<AppThemeType> = appSettingRepository.appSettingsFlow.map {
        it.appliedThemeType
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppThemeType.DEVICE
    )
}