package com.lalo.pixdev.ui.components.sprites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.annotation.DrawableRes
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource

@Composable
fun SpriteAnimation(
    @DrawableRes drawableResId: Int,
    frameCount: Int,
    startFrame: Int,
    endFrame: Int,
    animationScale: Float,
    modifier: Modifier = Modifier,
    animationDelayMillis: Long = 80L,
    loop: Boolean = false,
    onAnimationEnd: () -> Unit = {}
) {
    val context = LocalContext.current
    val spriteSheet: ImageBitmap = ImageBitmap.imageResource(context.resources, drawableResId)

    val frameWidth = 64
    val frameHeight = 64

    var currentFrame by remember { mutableStateOf(startFrame) }

    LaunchedEffect(drawableResId, startFrame, endFrame, animationDelayMillis, loop) {
        if (loop) {
            while (true) {
                for (i in startFrame..endFrame) {
                    currentFrame = i
                    delay(animationDelayMillis)
                }
            }
        } else {
            for (i in startFrame..endFrame) {
                currentFrame = i
                delay(animationDelayMillis)
            }
            onAnimationEnd()
        }
    }

    val currentFrameX = currentFrame * frameWidth
    val pixelArtCanvasDisplaySizeDp = (frameWidth * animationScale).dp

    Box(
        modifier = modifier
            .size(pixelArtCanvasDisplaySizeDp),
        contentAlignment = Alignment.Center
    ) {
        PixelArtCanvas(
            modifier = Modifier.size(pixelArtCanvasDisplaySizeDp),
            spriteSheet = spriteSheet,
            currentFrameX = currentFrameX,
            frameWidth = frameWidth,
            frameHeight = frameHeight
        )
    }
}

@Composable
fun FullSpriteAnimation(
    @DrawableRes drawableResId: Int,
    frameCount: Int,
    startFrame: Int,
    endFrame: Int,
    animationScale: Float = 8f,
    animationAreaWidth: Dp = (64 * 8).dp,
    animationAreaHeight: Dp = (64 * 8).dp,
    animationDelayMillis: Long = 80L,
    loop: Boolean = false,
    onAnimationEnd: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        SpriteAnimation(
            drawableResId = drawableResId,
            frameCount = frameCount,
            startFrame = startFrame,
            endFrame = endFrame,
            animationScale = animationScale,
            animationDelayMillis = animationDelayMillis,
            loop = loop,
            onAnimationEnd = onAnimationEnd,
            modifier = Modifier.size(width = animationAreaWidth, height = animationAreaHeight)
        )
    }
}