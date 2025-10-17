package jp.co.nintendo.design.system.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn

val ndsTargetFadeInContentTransform = ContentTransform(
    targetContentEnter = fadeIn(animationSpec = tween(420, delayMillis = 160)),
    initialContentExit = ExitTransition.None
)

val ndsNoneTransform = ContentTransform(
    targetContentEnter = EnterTransition.None,
    initialContentExit = ExitTransition.None
)