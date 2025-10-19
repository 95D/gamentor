package jp.co.nintendo.setting.model.app

import jp.co.nintendo.setting.model.app.theme.AppThemeType

/**
 * A domain model representing entire app settings
 */
data class AppSettings(
    val appliedThemeType: AppThemeType,
    val isShownAllMessageBubbles: Boolean
)