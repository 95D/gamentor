package dev.headwind.gamentor.theme

import dev.headwind.design.system.colors.DarkSemanticColors
import dev.headwind.design.system.colors.LightSemanticColors
import dev.headwind.design.system.colors.SakuraDarkSemanticColors
import dev.headwind.design.system.colors.SakuraLightSemanticColors
import dev.headwind.design.system.colors.SemanticColors
import dev.headwind.setting.model.app.theme.AppThemeType

/**
 * An utility class for deciding [SemanticColors] for [AppThemeType]
 */
object AppThemeDeterminant {
    fun decideSemanticColors(isSystemInDarkTheme: Boolean, themeType: AppThemeType): SemanticColors? =
        when (themeType) {
            AppThemeType.DEVICE -> null
            AppThemeType.LIGHT -> LightSemanticColors
            AppThemeType.DARK -> DarkSemanticColors
            AppThemeType.SAKURA -> getSakuraSemanticColors(isSystemInDarkTheme)
        }

    private fun getSakuraSemanticColors(isSystemInDarkTheme: Boolean): SemanticColors =
        if (isSystemInDarkTheme) {
            SakuraDarkSemanticColors
        } else {
            SakuraLightSemanticColors
        }
}