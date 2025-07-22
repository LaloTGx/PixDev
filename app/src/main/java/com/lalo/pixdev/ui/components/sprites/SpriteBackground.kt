package com.lalo.pixdev.ui.components.sprites

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.layout.aspectRatio

@Composable
fun SpriteBackground(
    drawableResId: Int,
    frameIndex: Int,
    frameWidth: Int,
    frameHeight: Int,
    scale: Float = 1f,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val spriteAspectRatio = frameWidth.toFloat() / frameHeight.toFloat()

    Box(
        modifier = modifier
            .clip(RectangleShape)
            .aspectRatio(ratio = spriteAspectRatio, matchHeightConstraintsFirst = false)
    ) {
        SpriteImage(
            drawableResId = drawableResId,
            frameIndex = frameIndex,
            frameWidth = frameWidth,
            frameHeight = frameHeight,
            scale = scale,
            modifier = Modifier.matchParentSize()
        )
        content()
    }
}