package com.lalo.pixdev.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.roundToInt
import androidx.compose.material3.Switch
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.utils.isValidHex
import com.lalo.pixdev.ui.utils.parseHexToColor

@Composable
fun HexColorInputDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorConfirmed: (Color) -> Unit
) {
    val context = LocalContext.current

    var showHexInput by remember { mutableStateOf(true) }

    var hexInput by remember { mutableStateOf(String.format("%08X", initialColor.toArgb())) }
    var isHexInputValid by remember { mutableStateOf(true) }

    var redInput by remember { mutableStateOf((initialColor.red * 255).roundToInt().toString()) }
    var greenInput by remember { mutableStateOf((initialColor.green * 255).roundToInt().toString()) }
    var blueInput by remember { mutableStateOf((initialColor.blue * 255).roundToInt().toString()) }
    var alphaInput by remember { mutableStateOf((initialColor.alpha * 255).roundToInt().toString()) }
    var isRgbInputValid by remember { mutableStateOf(true) }

    var colorToConfirm by remember { mutableStateOf(initialColor) }

    LaunchedEffect(initialColor) {
        hexInput = String.format("%08X", initialColor.toArgb())
        redInput = (initialColor.red * 255).roundToInt().toString()
        greenInput = (initialColor.green * 255).roundToInt().toString()
        blueInput = (initialColor.blue * 255).roundToInt().toString()
        alphaInput = (initialColor.alpha * 255).roundToInt().toString()

        isHexInputValid = isValidHex(hexInput)
        isRgbInputValid = isValidRgbComponent(redInput) && isValidRgbComponent(greenInput) &&
                isValidRgbComponent(blueInput) && isValidRgbComponent(alphaInput)
        colorToConfirm = initialColor
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(context.getString(R.string.color_input_title)) },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(context.getString(R.string.use_rgb_switch))
                    Switch(
                        checked = !showHexInput,
                        onCheckedChange = { isChecked ->
                            showHexInput = !isChecked
                            if (showHexInput) {
                                hexInput = String.format("%08X", colorToConfirm.toArgb())
                                isHexInputValid = isValidHex(hexInput)
                            } else {
                                redInput = (colorToConfirm.red * 255).roundToInt().toString()
                                greenInput = (colorToConfirm.green * 255).roundToInt().toString()
                                blueInput = (colorToConfirm.blue * 255).roundToInt().toString()
                                alphaInput = (colorToConfirm.alpha * 255).roundToInt().toString()
                                isRgbInputValid = isValidRgbComponent(redInput) && isValidRgbComponent(greenInput) &&
                                        isValidRgbComponent(blueInput) && isValidRgbComponent(alphaInput)
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (showHexInput) {
                    OutlinedTextField(
                        value = hexInput,
                        onValueChange = { newValue ->
                            val filteredValue = newValue.uppercase().filter { it.isDigit() || (it >= 'A' && it <= 'F') }
                            hexInput = filteredValue.take(8)

                            val parsedColor = parseHexToColor(hexInput)
                            if (parsedColor != null) {
                                isHexInputValid = true
                                colorToConfirm = parsedColor
                            } else {
                                isHexInputValid = false
                            }
                        },
                        label = { Text(context.getString(R.string.hex_value_label)) },
                        placeholder = { Text(context.getString(R.string.hex_placeholder))},
                        isError = !isHexInputValid && hexInput.isNotEmpty(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (!isHexInputValid && hexInput.isNotEmpty()) {
                        Text(
                            text = context.getString(R.string.invalid_hex_format_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else {
                    Text(context.getString(R.string.rgb_values_label), style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        RgbInputField(
                            label = "R:",
                            value = redInput,
                            onValueChange = { newValue ->
                                redInput = newValue.filter { it.isDigit() }.take(3)
                                isRgbInputValid = isValidRgbComponent(redInput) && isValidRgbComponent(greenInput) &&
                                        isValidRgbComponent(blueInput) && isValidRgbComponent(alphaInput)
                                if (isRgbInputValid) {
                                    colorToConfirm = Color(
                                        redInput.toInt(), greenInput.toInt(), blueInput.toInt(), alphaInput.toInt()
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        RgbInputField(
                            label = "G:",
                            value = greenInput,
                            onValueChange = { newValue ->
                                greenInput = newValue.filter { it.isDigit() }.take(3)
                                isRgbInputValid = isValidRgbComponent(redInput) && isValidRgbComponent(greenInput) &&
                                        isValidRgbComponent(blueInput) && isValidRgbComponent(alphaInput)
                                if (isRgbInputValid) {
                                    colorToConfirm = Color(
                                        redInput.toInt(), greenInput.toInt(), blueInput.toInt(), alphaInput.toInt()
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        RgbInputField(
                            label = "B:",
                            value = blueInput,
                            onValueChange = { newValue ->
                                blueInput = newValue.filter { it.isDigit() }.take(3)
                                isRgbInputValid = isValidRgbComponent(redInput) && isValidRgbComponent(greenInput) &&
                                        isValidRgbComponent(blueInput) && isValidRgbComponent(alphaInput)
                                if (isRgbInputValid) {
                                    colorToConfirm = Color(
                                        redInput.toInt(), greenInput.toInt(), blueInput.toInt(), alphaInput.toInt()
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        RgbInputField(
                            label = "A:",
                            value = alphaInput,
                            onValueChange = { newValue ->
                                alphaInput = newValue.filter { it.isDigit() }.take(3)
                                isRgbInputValid = isValidRgbComponent(redInput) && isValidRgbComponent(greenInput) &&
                                        isValidRgbComponent(blueInput) && isValidRgbComponent(alphaInput)
                                if (isRgbInputValid) {
                                    colorToConfirm = Color(
                                        redInput.toInt(), greenInput.toInt(), blueInput.toInt(), alphaInput.toInt()
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (!isRgbInputValid && (redInput.isNotEmpty() || greenInput.isNotEmpty() || blueInput.isNotEmpty() || alphaInput.isNotEmpty())) {
                        Text(
                            text = context.getString(R.string.invalid_rgb_alpha_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = colorToConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = context.getString(R.string.current_color_display, String.format("#%08X", colorToConfirm.toArgb())),
                        color = if (colorToConfirm.luminance() > 0.5) Color.Black else Color.White,
                        modifier = Modifier
                            .padding(16.dp)
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onColorConfirmed(colorToConfirm)
                },
                enabled = (showHexInput && isHexInputValid && hexInput.isNotBlank()) ||
                        (!showHexInput && isRgbInputValid && redInput.isNotBlank() && greenInput.isNotBlank() && blueInput.isNotBlank() && alphaInput.isNotBlank())
            ) {
                Text(context.getString(R.string.add_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(context.getString(R.string.cancel_button))
            }
        }
    )
}

@Composable
fun RgbInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue.filter { it.isDigit() }.take(3))
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
        modifier = modifier
    )
}

fun isValidRgbComponent(input: String): Boolean {
    val value = input.toIntOrNull() ?: return false
    return value in 0..255
}