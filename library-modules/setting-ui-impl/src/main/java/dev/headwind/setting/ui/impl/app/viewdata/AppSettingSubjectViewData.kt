package dev.headwind.setting.ui.impl.app.viewdata

import androidx.annotation.StringRes

/**
 * A view data class of one subject in settings
 */
data class AppSettingSubjectViewData(
    @param:StringRes val name: Int,
    val items: List<AppSettingItemViewData>
)