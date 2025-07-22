package com.lalo.pixdev.ui.components.sprites

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

@Composable
fun PixelArtCanvas(
    modifier: Modifier = Modifier,
    spriteSheet: ImageBitmap,
    currentFrameX: Int,
    frameWidth: Int,
    frameHeight: Int,
) {
    val pixelArtPaint = remember {
        Paint().asFrameworkPaint().apply {
            isFilterBitmap = false
        }
    }

    Canvas(
        modifier = modifier
    ) {
        val targetWidthPx = size.width
        val targetHeightPx = size.height

        val offsetX = (size.width - targetWidthPx) / 2
        val offsetY = (size.height - targetHeightPx) / 2

        val srcRect = Rect(currentFrameX, 0, currentFrameX + frameWidth, frameHeight)

        val dstRect = Rect(0, 0, targetWidthPx.toInt(), targetHeightPx.toInt())

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawBitmap(
                spriteSheet.asAndroidBitmap(),
                srcRect,
                dstRect,
                pixelArtPaint
            )
        }
    }
}