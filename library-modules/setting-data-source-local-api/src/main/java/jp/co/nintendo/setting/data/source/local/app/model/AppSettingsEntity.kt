package jp.co.nintendo.setting.data.source.local.app.model

import kotlinx.serialization.Serializable

/**
 * A data model representing entire setting configurations to be stored in data store
 */
@Serializable
data class AppSettingsEntity(
    val appliedThemeType: String,
    val isShownAllMessageBubbles: Boolean
) {
    companion object {
        val DEFAULT: AppSettingsEntity = AppSettingsEntity(
            appliedThemeType = "",
            isShownAllMessageBubbles = false
        )
    }
}