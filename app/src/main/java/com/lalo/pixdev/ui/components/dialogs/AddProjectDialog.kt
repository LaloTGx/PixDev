package com.lalo.pixdev.ui.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.components.sprites.SpriteImage
import com.lalo.pixdev.ui.components.sprites.bouncyClickable
import com.lalo.pixdev.viewmodel.model.Project
import androidx.compose.material3.OutlinedTextFieldDefaults

@Composable
fun AddProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    initialProject: Project? = null,
    dialogTitle: String
) {
    val context = LocalContext.current
    var projectName by remember { mutableStateOf(initialProject?.name ?: "") }
    val coroutineScope = rememberCoroutineScope()
    var showContent by remember { mutableStateOf(false) }

    val isCreationMode = initialProject == null

    val MAX_LENGTH = 15

    val normalSpriteIconResId = R.drawable.pixicon
    val pressedSpriteIconResId = R.drawable.pixicon_press

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
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 4.dp)
                        .clip(RoundedCornerShape(0.dp)),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .navigationBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(MaterialTheme.colorScheme.onBackground)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(top = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dialogTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = MaterialTheme.colorScheme.onBackground
                                    ),
                                )
                                if (isCreationMode) {
                                    Text(
                                        text = stringResource(R.string.home_dialog_note_message),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = Bold
                                        )
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val cancelInteractionSource = remember { MutableInteractionSource() }
                                val cancelIsPressed by cancelInteractionSource.collectIsPressedAsState()
                                val currentCancelIconResId = if (cancelIsPressed) pressedSpriteIconResId else normalSpriteIconResId

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
                                            pressedDarkenFactor = 0f,
                                            unpressedScale = 1f,
                                            interactionSource = cancelInteractionSource
                                        )
                                )

                                Spacer(Modifier.width(5.dp))

                                val confirmInteractionSource = remember { MutableInteractionSource() }
                                val confirmIsPressed by confirmInteractionSource.collectIsPressedAsState()
                                val currentConfirmIconResId = if (confirmIsPressed) pressedSpriteIconResId else normalSpriteIconResId

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
                                                    onConfirm(projectName)
                                                }
                                            },
                                            enabled = projectName.isNotBlank(),
                                            pressedScale = 0.9f,
                                            pressedDarkenFactor = 0f,
                                            unpressedScale = 1f,
                                            interactionSource = confirmInteractionSource
                                        )
                                )
                            }
                        }

                        OutlinedTextField(
                            value = projectName,
                            onValueChange = { newValue ->
                                if (newValue.length <= MAX_LENGTH) {
                                    projectName = newValue
                                }
                            },
                            label = { Text(context.getString(R.string.project_name_label)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top= 5.dp, bottom = 50.dp)
                                .background(MaterialTheme.colorScheme.background)
                                .clip(RoundedCornerShape(0.dp)),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                cursorColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                focusedLabelColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}