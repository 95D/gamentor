package dev.headwind.design.system.colors

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class SemanticColors(
    val surfacePrimary: Color,
    val surfaceSecondary: Color,
    val header: Color,
    val headerText: Color,
    val textDefault: Color,
    val textAccent: Color,
    val textSubject: Color,
    val buttonPrimaryText: Color,
    val buttonPrimary: Color,
    val background: Color,
    val backgroundText: Color,
    val settingActive: Color,
    val messageBubbleOutlineNone: Color,
    val messageBubbleOutlineOthers: Color,
    val messageBubbleMine: Color,
    val messageBubbleOthers: Color,
    val messageBubbleSystem: Color,
    val messageBubbleDeveloper: Color,
    val messageBubbleTextNormal: Color,
    val messageBubbleTextDeveloper: Color,
    val messageBubbleTextSystem: Color
)

val LightSemanticColors = SemanticColors(
    surfacePrimary = GrayF0Alpha100,
    surfaceSecondary = GrayE6Alpha100,
    header = Transparent,
    headerText = Gray30Alpha100,
    textDefault = Gray12Alpha100,
    textAccent = AccentSkyBlue,
    textSubject = Gray52Alpha100,
    buttonPrimary = NintendoRed,
    buttonPrimaryText = GrayFAAlpha100,
    background = Gray98Alpha100,
    backgroundText = Gray30Alpha100,
    settingActive = AccentSkyBlue,
    messageBubbleOutlineNone = Transparent,
    messageBubbleOutlineOthers = DarkGray,
    messageBubbleMine = AccentSkyRed,
    messageBubbleOthers = GrayE6Alpha100,
    messageBubbleSystem = Gray20Alpha72,
    messageBubbleDeveloper = AccentSkyBlue,
    messageBubbleTextNormal = Gray12Alpha100,
    messageBubbleTextDeveloper = AccentSkyRed,
    messageBubbleTextSystem = GrayFAAlpha100
)

val DarkSemanticColors = SemanticColors(
    surfacePrimary = Gray12Alpha100,
    surfaceSecondary = Gray30Alpha100,
    header = Transparent,
    headerText = GrayFAAlpha100,
    textDefault = GrayFAAlpha100,
    textAccent = AccentSkyBlue,
    textSubject = Gray98Alpha100,
    buttonPrimaryText = GrayFAAlpha100,
    buttonPrimary = NintendoRed,
    background = DarkGray,
    backgroundText = Gray98Alpha100,
    settingActive = AccentSkyBlue,
    messageBubbleOutlineNone = Transparent,
    messageBubbleOutlineOthers = DarkGray,
    messageBubbleMine = AccentSkyRed,
    messageBubbleOthers = GrayE6Alpha100,
    messageBubbleSystem = Gray20Alpha72,
    messageBubbleDeveloper = AccentSkyBlue,
    messageBubbleTextNormal = Gray12Alpha100,
    messageBubbleTextDeveloper = AccentSkyRed,
    messageBubbleTextSystem = GrayFAAlpha100
)