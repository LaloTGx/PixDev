package com.lalo.pixdev.ui.components.generalscomp

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun CustomPixelSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderWidth = 2.dp
    val switchHeight = 25.dp
    val switchWidth = 50.dp
    val thumbSize = 25.dp

    val thumbColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.onBackground else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "ThumbColorAnimation"
    )

    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "ThumbOffsetAnimation"
    )

    Box(
        modifier = modifier
            .size(width = switchWidth, height = switchHeight)
            .border(borderWidth, MaterialTheme.colorScheme.onBackground)
            .background(MaterialTheme.colorScheme.background)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(thumbSize)
                .background(thumbColor)
                .graphicsLayer {
                    translationX = thumbOffset * (switchWidth - thumbSize).toPx()
                }
                .border(
                    width = borderWidth,
                    color = MaterialTheme.colorScheme.onBackground
                )
        )
    }
}