package jp.co.nintendo.design.system.theme

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import jp.co.nintendo.design.system.colors.DarkSemanticColors
import jp.co.nintendo.design.system.colors.LightSemanticColors
import jp.co.nintendo.design.system.colors.mapToColorScheme

val LocalAppSemanticColors = staticCompositionLocalOf {
    LightSemanticColors
}

@Composable
fun AppTheme(
    isDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val semanticColors = if (isDarkTheme) DarkSemanticColors else LightSemanticColors
    val materialColorScheme = mapToColorScheme(semanticColors, isDarkTheme)

    CompositionLocalProvider(
        LocalAppSemanticColors provides semanticColors
    ) {
        val view = LocalView.current
        val window = LocalActivity.current?.window
        SideEffect {
            if (window != null) {
                WindowCompat.getInsetsController(window, view)
                    .isAppearanceLightStatusBars = !isDarkTheme
            }
        }
        MaterialTheme(
            colorScheme = materialColorScheme,
            content = content
        )
    }
}
