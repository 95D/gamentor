package jp.co.nintendo.design.system.colors

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class SemanticColors(
    val surfacePrimary: Color,
    val header: Color,
    val headerText: Color,
    val textDefault: Color,
    val textAccent: Color,
    val buttonPrimaryText: Color,
    val buttonPrimary: Color,
    val background: Color,
    val backgroundText: Color,
    val messageBubbleMine: Color,
    val messageBubbleOthers: Color,
    val messageBubbleOthersOutline: Color,
    val messageBubbleText: Color
)

val LightSemanticColors = SemanticColors(
    surfacePrimary = GrayF0Alpha100,
    header = NintendoRed,
    headerText = GrayFAAlpha100,
    textDefault = Gray30Alpha100,
    textAccent = AccentSkyBlue,
    buttonPrimary = NintendoRed,
    buttonPrimaryText = GrayFAAlpha100,
    background = Gray98Alpha100,
    backgroundText = Gray30Alpha100,
    messageBubbleMine = AccentSkyRed,
    messageBubbleOthers = GrayE6Alpha100,
    messageBubbleOthersOutline = DarkGray,
    messageBubbleText = DarkGray
)

val DarkSemanticColors = SemanticColors(
    surfacePrimary = Gray12Alpha100,
    header = NintendoRed,
    headerText = Gray30Alpha100,
    textDefault = GrayFAAlpha100,
    textAccent = AccentSkyBlue,
    buttonPrimaryText = GrayFAAlpha100,
    buttonPrimary = NintendoRed,
    background = DarkGray,
    backgroundText = Gray98Alpha100,
    messageBubbleMine = AccentSkyRed,
    messageBubbleOthers = GrayE6Alpha100,
    messageBubbleOthersOutline = DarkGray,
    messageBubbleText = DarkGray
)