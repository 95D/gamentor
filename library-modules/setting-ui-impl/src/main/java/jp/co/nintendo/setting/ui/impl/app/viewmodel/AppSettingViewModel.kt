package jp.co.nintendo.setting.ui.impl.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.nintendo.setting.data.repository.app.AppSettingRepository
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingItemKey
import jp.co.nintendo.setting.ui.impl.app.viewdata.AppSettingItemViewData
import jp.co.nintendo.setting.ui.impl.app.viewmodel.factory.AppSettingSubjectViewDataFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A view model for managing state of app setting screen
 */
@HiltViewModel
class AppSettingViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository,
    private val appSettingSubjectViewDataFactory: AppSettingSubjectViewDataFactory
) : ViewModel() {
    val settingItemsStateFlow: StateFlow<List<AppSettingItemViewData>> =
        appSettingRepository.appSettingsFlow.map {
            val subjects = appSettingSubjectViewDataFactory.create(
                it
            )
            appSettingSubjectViewDataFactory.createFlattenSettingItems(subjects)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun clickSettingItemKey(itemKey: AppSettingItemKey) {
        when (itemKey) {
            AppSettingItemKey.SHOW_ALL_MESSAGES -> toggleShowAllMessages()
        }
    }

    private fun toggleShowAllMessages() {
        viewModelScope.launch {
            val wasShown = appSettingRepository.appSettingsFlow.first()
                .isShownAllMessageBubbles
            appSettingRepository.updateIsShownAllMessageBubbles(!wasShown)
        }
    }
}