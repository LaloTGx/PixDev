package com.lalo.pixdev.ui.components.requerimentcomp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.components.sprites.SpriteImage
import com.lalo.pixdev.ui.components.sprites.bouncyClickable
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import androidx.compose.foundation.border
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle

enum class RequirementPriority {
    OPTIONAL, MANDATORY, ESSENTIAL;

    fun getSpriteFrameIndex(): Int {
        return when (this) {
            OPTIONAL -> 9
            MANDATORY -> 10
            ESSENTIAL -> 11
        }
    }
}

@Composable
fun RequirementInputPanel(
    onAddRequirement: (
        name: String,
        priority: RequirementPriority,
        dateTimeMillis: Long
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val normalPixIconResId = R.drawable.pixicon
    val pressedPixIconResId = R.drawable.pixicon_press

    val initialCombinedDateTime = remember { LocalDateTime.now().withSecond(0).withNano(0) }

    var requirementName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(initialCombinedDateTime.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(initialCombinedDateTime.toLocalTime()) }

    var selectedPriorityIndex by remember { mutableIntStateOf(0) }
    val priorities = remember { RequirementPriority.values() }

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var hasShownLengthToast by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DateTimeSelectionButton(
                text = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                onClick = { showTimePickerDialog = true },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(80.dp),
                textStyle = MaterialTheme.typography.titleLarge.copy(fontSize = 80.sp, textAlign = TextAlign.Center),
                hasBorder = false
            )
            Spacer(modifier = Modifier.height(16.dp))

            DateTimeSelectionButton(
                text = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                onClick = { showDatePickerDialog = true },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy( fontSize = 20.sp, textAlign = TextAlign.Center),
                hasBorder = false
            )
        }

        if (showDatePickerDialog) {
            val datePickerDialog = remember {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                        showDatePickerDialog = false
                    },
                    selectedDate.year,
                    selectedDate.monthValue - 1,
                    selectedDate.dayOfMonth
                )
            }
            DisposableEffect(Unit) {
                datePickerDialog.setOnDismissListener { showDatePickerDialog = false }
                datePickerDialog.show()
                onDispose { datePickerDialog.dismiss() }
            }
        }

        if (showTimePickerDialog) {
            val timePickerDialog = remember {
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        selectedTime = LocalTime.of(hourOfDay, minute).withSecond(0).withNano(0)
                        showTimePickerDialog = false
                    },
                    selectedTime.hour,
                    selectedTime.minute,
                    true
                )
            }
            DisposableEffect(Unit) {
                timePickerDialog.setOnDismissListener { showTimePickerDialog = false }
                timePickerDialog.show()
                onDispose { timePickerDialog.dismiss() }
            }
        }

        AnimatedVisibility(
            visible = true,
            enter = slideInHorizontally(
                initialOffsetX = { 0 },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = requirementName,
                        onValueChange = { newValue ->
                            if (newValue.length <= 25) {
                                requirementName = newValue
                                hasShownLengthToast = false
                            } else {
                                if (!hasShownLengthToast) {
                                    Toast.makeText(context, "El nombre no puede exceder los 25 caracteres", Toast.LENGTH_SHORT).show()
                                    hasShownLengthToast = true
                                }
                            }
                        },
                        label = { Text(context.getString(R.string.requirement_name_label)) },
                        modifier = Modifier
                            .weight(1f)
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
                    Spacer(modifier = Modifier.width(8.dp))

                    val addRequirementInteractionSource = remember { MutableInteractionSource() }
                    val addRequirementIsPressed by addRequirementInteractionSource.collectIsPressedAsState()
                    val currentAddRequirementIconResId = if (addRequirementIsPressed) pressedPixIconResId else normalPixIconResId

                    SpriteImage(
                        drawableResId = currentAddRequirementIconResId,
                        frameIndex = 1,
                        frameHeight = 64,
                        frameWidth = 64,
                        scale = 0.7f,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(0.dp))
                            .bouncyClickable(
                                onClick = {
                                    if (requirementName.isNotBlank()) {
                                        val nowTruncatedToMinute = LocalDateTime.now().withSecond(0).withNano(0)
                                        val combinedDateTime = LocalDateTime.of(selectedDate, selectedTime)
                                            .withSecond(0)
                                            .withNano(0)

                                        if (combinedDateTime.isBefore(nowTruncatedToMinute)) {
                                            Toast.makeText(
                                                context,
                                                "No puedes crear requerimientos para fechas y horas pasadas. Por favor, selecciona una fecha y hora futura.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            return@bouncyClickable
                                        }

                                        val maxFutureDateTime = nowTruncatedToMinute.plusYears(2)
                                        if (combinedDateTime.isAfter(maxFutureDateTime)) {
                                            Toast.makeText(context, "No puedes crear requerimientos para más de 2 años en el futuro (máximo hasta ${maxFutureDateTime.year}).", Toast.LENGTH_LONG).show()
                                            return@bouncyClickable
                                        }

                                        val dateTimeMillis = combinedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                                        coroutineScope.launch {
                                            keyboardController?.hide()
                                            kotlinx.coroutines.delay(300)
                                            onAddRequirement(requirementName, priorities[selectedPriorityIndex], dateTimeMillis)
                                            requirementName = ""
                                            selectedPriorityIndex = 0
                                            hasShownLengthToast = false
                                        }
                                    }
                                },
                                enabled = requirementName.isNotBlank(),
                                pressedScale = 0.9f,
                                pressedDarkenFactor = 0f,
                                unpressedScale = 1f,
                                interactionSource = addRequirementInteractionSource
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    val semaphoreInteractionSource = remember { MutableInteractionSource() }
                    val semaphoreIsPressed by semaphoreInteractionSource.collectIsPressedAsState()

                    val currentSemaphoreResId = if (semaphoreIsPressed) pressedPixIconResId else normalPixIconResId
                    val currentSemaphoreFrameIndex = priorities[selectedPriorityIndex].getSpriteFrameIndex()

                    SpriteImage(
                        drawableResId = currentSemaphoreResId,
                        frameIndex = currentSemaphoreFrameIndex,
                        frameHeight = 64,
                        frameWidth = 64,
                        scale = 0.7f,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(0.dp))
                            .bouncyClickable(
                                onClick = {
                                    selectedPriorityIndex = (selectedPriorityIndex + 1) % priorities.size
                                },
                                pressedScale = 0.9f,
                                pressedDarkenFactor = 0f,
                                unpressedScale = 1f,
                                interactionSource = semaphoreInteractionSource
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun DateTimeSelectionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    hasBorder: Boolean = true
) {
    val borderColor = MaterialTheme.colorScheme.onBackground
    val backgroundColor = MaterialTheme.colorScheme.background
    val interactionSource = remember { MutableInteractionSource() }

    val buttonModifier = if (hasBorder) {
        modifier
            .border(6.dp, borderColor)
            .background(backgroundColor)
    } else {
        modifier.background(backgroundColor)
    }

    Box(
        modifier = buttonModifier
            .clip(RoundedCornerShape(0.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            style = textStyle
        )
    }
}