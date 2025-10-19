package jp.co.nintendo.setting.data.repository.app

import jp.co.nintendo.setting.model.app.AppSettings
import jp.co.nintendo.setting.model.app.theme.AppThemeType
import kotlinx.coroutines.flow.Flow

/**
 * A repository class for managing setting configuration uniquely defined in the app
 */
interface AppSettingRepository {
    val appSettingsFlow: Flow<AppSettings>

    suspend fun updateApplyThemeType(themeType: AppThemeType)

    suspend fun updateIsShownAllMessageBubbles(isShown: Boolean)
}