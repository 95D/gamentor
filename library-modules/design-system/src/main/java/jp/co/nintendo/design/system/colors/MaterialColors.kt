package jp.co.nintendo.design.system.colors

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

internal fun mapToColorScheme(semanticColors: SemanticColors, isDark: Boolean): ColorScheme {
    return if (isDark) {
        mapToLightColorScheme(semanticColors)
    } else {
        mapToDarkColorScheme(semanticColors)
    }
}

private fun mapToLightColorScheme(semanticColors: SemanticColors): ColorScheme {
    return lightColorScheme(
        primary = semanticColors.buttonPrimary,
        onPrimary = semanticColors.buttonPrimaryText,

        background = semanticColors.surfacePrimary,
        onBackground = semanticColors.textDefault,

        surface = semanticColors.surfacePrimary,
        onSurface = semanticColors.textDefault
    )
}

private fun mapToDarkColorScheme(semanticColors: SemanticColors): ColorScheme {
    return darkColorScheme(
        primary = semanticColors.buttonPrimary,
        onPrimary = semanticColors.buttonPrimaryText,

        background = semanticColors.surfacePrimary,
        onBackground = semanticColors.textDefault,

        surface = semanticColors.surfacePrimary,
        onSurface = semanticColors.textDefault
    )
}