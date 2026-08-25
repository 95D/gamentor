package dev.headwind.setting.model.app

import dev.headwind.setting.model.app.chess.ChessUnit
import dev.headwind.setting.model.app.theme.AppThemeType

/**
 * A domain model representing entire app settings
 */
data class AppSettings(
    val appliedThemeType: AppThemeType,
    val isShownAllMessageBubbles: Boolean,
    val simulatedChessUnits: List<ChessUnit>
)