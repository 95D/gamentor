package jp.co.nintendo.gamentor.theme

import jp.co.nintendo.design.system.colors.DarkSemanticColors
import jp.co.nintendo.design.system.colors.LightSemanticColors
import jp.co.nintendo.design.system.colors.SakuraDarkSemanticColors
import jp.co.nintendo.design.system.colors.SakuraLightSemanticColors
import jp.co.nintendo.design.system.colors.SemanticColors
import jp.co.nintendo.setting.model.app.theme.AppThemeType

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