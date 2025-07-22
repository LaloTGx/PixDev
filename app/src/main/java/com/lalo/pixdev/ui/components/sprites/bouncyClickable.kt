package com.lalo.pixdev.ui.components.sprites

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.bouncyClickable(
    onClick: () -> Unit,
    enabled: Boolean = true,
    pressedScale: Float = 0.8f,
    pressedDarkenFactor: Float = 0.4f,
    unpressedScale: Float = 1f,
    interactionSource: MutableInteractionSource = MutableInteractionSource()
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()

    val targetScale = if (enabled && isPressed) pressedScale else unpressedScale
    val targetAlpha = if (enabled && isPressed) 1.0f - pressedDarkenFactor else 1.0f

    val currentScale by animateFloatAsState(
        targetValue = targetScale,
        label = "bouncyScaleAnimation"
    )

    val currentAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        label = "bouncyDarkenAnimation"
    )

    this
        .graphicsLayer {
            scaleX = currentScale
            scaleY = currentScale
            alpha = currentAlpha
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}