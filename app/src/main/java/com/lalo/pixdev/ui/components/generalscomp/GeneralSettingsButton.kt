package com.lalo.pixdev.ui.components.generalscomp

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lalo.pixdev.navigation.Routes
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.components.sprites.SpriteImage
import com.lalo.pixdev.ui.components.sprites.bouncyClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun GeneralSettingsButton(navController: NavController, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val normalIconResId = R.drawable.pixicon
    val pressedIconResId = R.drawable.pixicon_press
    val currentIconResId = if (isPressed) pressedIconResId else normalIconResId

    SpriteImage(
        drawableResId = currentIconResId,
        frameIndex = 0,
        frameHeight = 64,
        frameWidth = 64,
        scale = 0.7f,
        modifier = modifier
            .clip(RoundedCornerShape(0.dp))
            .bouncyClickable(
                onClick = { navController.navigate(Routes.SETTINGS) },
                pressedScale = 0.9f,
                pressedDarkenFactor = 0f,
                unpressedScale = 1f,
                interactionSource = interactionSource
            )
    )
}