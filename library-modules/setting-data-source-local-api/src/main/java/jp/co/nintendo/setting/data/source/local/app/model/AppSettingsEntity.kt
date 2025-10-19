package jp.co.nintendo.setting.data.source.local.app.model

import jp.co.nintendo.setting.data.source.local.app.model.chess.ChessUnitEntity
import kotlinx.serialization.Serializable

/**
 * A data model representing entire setting configurations to be stored in data store
 */
@Serializable
data class AppSettingsEntity(
    val appliedThemeType: String,
    val isShownAllMessageBubbles: Boolean,
    val simulatedChessUnits: List<ChessUnitEntity>
) {
    companion object {
        val DEFAULT: AppSettingsEntity = AppSettingsEntity(
            appliedThemeType = "",
            isShownAllMessageBubbles = false,
            simulatedChessUnits = emptyList()
        )
    }
}