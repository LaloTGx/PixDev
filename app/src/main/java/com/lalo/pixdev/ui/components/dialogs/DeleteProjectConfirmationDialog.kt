package com.lalo.pixdev.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.components.sprites.SpriteImage
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.lalo.pixdev.ui.components.sprites.bouncyClickable

@Composable
fun DeleteProjectConfirmationDialog(
    projectName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showContent by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val normalPixIconResId = R.drawable.pixicon
    val pressedPixIconResId = R.drawable.pixicon_press

    LaunchedEffect(Unit) {
        showContent = true
    }

    Dialog(
        onDismissRequest = {
            coroutineScope.launch {
                showContent = false
                delay(300)
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = showContent,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    coroutineScope.launch {
                        showContent = false
                        delay(300)
                        onDismiss()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = 0.5f,
                        stiffness = 200f
                    ),
                    initialScale = 0.1f
                ),
                exit = scaleOut(
                    animationSpec = spring(
                        dampingRatio = 0.5f,
                        stiffness = 200f
                    ),
                    targetScale = 0.1f
                )
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth(0.9f)
                        .background(MaterialTheme.colorScheme.background)
                        .border(6.dp, MaterialTheme.colorScheme.onBackground)
                        .padding(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = context.getString(R.string.delete_project_dialog_title, projectName),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = context.getString(R.string.delete_project_confirmation_text, projectName),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val cancelInteractionSource = remember { MutableInteractionSource() }
                        val cancelIsPressed by cancelInteractionSource.collectIsPressedAsState()
                        val currentCancelIconResId = if (cancelIsPressed) pressedPixIconResId else normalPixIconResId

                        SpriteImage(
                            drawableResId = currentCancelIconResId,
                            frameIndex = 6,
                            frameHeight = 64,
                            frameWidth = 64,
                            scale = 0.7f,
                            modifier = Modifier
                                .clip(RoundedCornerShape(0.dp))
                                .bouncyClickable(
                                    onClick = {
                                        coroutineScope.launch {
                                            showContent = false
                                            delay(300)
                                            onDismiss()
                                        }
                                    },
                                    pressedScale = 0.9f,
                                    pressedDarkenFactor = 0.3f,
                                    unpressedScale = 1f,
                                    interactionSource = cancelInteractionSource
                                )
                        )

                        Spacer(Modifier.width(16.dp))

                        val confirmInteractionSource = remember { MutableInteractionSource() }
                        val confirmIsPressed by confirmInteractionSource.collectIsPressedAsState()
                        val currentConfirmIconResId = if (confirmIsPressed) pressedPixIconResId else normalPixIconResId

                        SpriteImage(
                            drawableResId = currentConfirmIconResId,
                            frameIndex = 5,
                            frameHeight = 64,
                            frameWidth = 64,
                            scale = 0.7f,
                            modifier = Modifier
                                .clip(RoundedCornerShape(0.dp))
                                .bouncyClickable(
                                    onClick = {
                                        coroutineScope.launch {
                                            showContent = false
                                            delay(300)
                                            onConfirm()
                                        }
                                    },
                                    pressedScale = 0.9f,
                                    pressedDarkenFactor = 0.3f,
                                    unpressedScale = 1f,
                                    interactionSource = confirmInteractionSource
                                )
                        )
                    }
                }
            }
        }
    }
}