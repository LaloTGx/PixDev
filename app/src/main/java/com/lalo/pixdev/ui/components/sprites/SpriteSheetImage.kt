package com.lalo.pixdev.ui.components.sprites

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp

@Composable
fun SpriteImage(
    drawableResId: Int,
    frameIndex: Int,
    frameWidth: Int,
    frameHeight: Int,
    scale: Float = 4f,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val spriteSheetBitmap: ImageBitmap = ImageBitmap.imageResource(context.resources, drawableResId)

    val currentFrameX = frameIndex * frameWidth

    val displayWidthDp = (frameWidth * scale).dp
    val displayHeightDp = (frameHeight * scale).dp

    PixelArtCanvas(
        modifier = modifier.size(width = displayWidthDp, height = displayHeightDp),
        spriteSheet = spriteSheetBitmap,
        currentFrameX = currentFrameX,
        frameWidth = frameWidth,
        frameHeight = frameHeight
    )
}