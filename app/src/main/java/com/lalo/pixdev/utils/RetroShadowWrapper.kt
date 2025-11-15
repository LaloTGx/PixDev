package com.lalo.pixdev.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RetroShadowWrapper(
    modifier: Modifier = Modifier,
    shadowOffset: Dp = 4.dp,
    shadowColor: Color = Color.Black.copy(alpha = 0.8f),
    shape: Shape = RoundedCornerShape(3.dp),
    content: @Composable (contentModifier: Modifier) -> Unit
) {
    Box(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .clip(shape)
                .background(shadowColor)
        )

        content(
            Modifier
                .matchParentSize()
                .offset(x = -shadowOffset, y = -shadowOffset)
        )
    }
}