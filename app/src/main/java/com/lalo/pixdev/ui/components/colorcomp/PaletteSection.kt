package com.lalo.pixdev.ui.components.colorcomp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.lalo.pixdev.viewmodel.ColorViewModel
import com.lalo.pixdev.ui.utils.isValidHex
import com.lalo.pixdev.ui.utils.parseHexToColor
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.lalo.pixdev.R

@Composable
fun PaletteSection(
    viewModel: ColorViewModel,
    projectId: String,
    modifier: Modifier = Modifier,
    onAddColorToPalette: (paletteIndex: Int, colorIndex: Int, color: Color) -> Unit,
    onSelectColorFromPalette: (color: Color) -> Unit,
    onReplaceColorInPalette: (paletteIndex: Int, colorIndex: Int, newColor: Color) -> Unit
) {
    val selectedColor = viewModel.selectedColor.collectAsStateWithLifecycle().value
    val palettes = viewModel.palettes.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(projectId) {
        viewModel.loadPalettesForProject(projectId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 10.dp)
    ) {

        palettes.forEachIndexed { paletteIndex, paletteColorsFlow ->
            val paletteColors = paletteColorsFlow.collectAsStateWithLifecycle().value

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(context.getString(R.string.palette_number, paletteIndex + 1), style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                paletteColors.forEachIndexed { colorIndex, color ->
                    val isTransparent = color == Color.Transparent
                    PaletteColorBlock(
                        color = color,
                        modifier = Modifier
                            .weight(0.5f)
                            .heightIn(max = 60.dp)
                            .padding(4.dp),
                        onColorClick = {
                            if (isTransparent) {
                                val newColors = paletteColors.toMutableList().apply { this[colorIndex] = selectedColor }
                                viewModel.savePalette(projectId, paletteIndex, newColors)
                            } else {
                                onSelectColorFromPalette(color)
                            }
                        },
                        onColorLongPress = {
                            val newColors = paletteColors.toMutableList().apply { this[colorIndex] = selectedColor }
                            viewModel.savePalette(projectId, paletteIndex, newColors)
                        },
                        onColorDoubleClickPaste = { pastedText ->
                            val cleanHex = pastedText.removePrefix("#").uppercase()

                            if (isValidHex(cleanHex)) {
                                val parsedColor = parseHexToColor(cleanHex)
                                if (parsedColor != null) {
                                    val newColors = paletteColors.toMutableList().apply { this[colorIndex] = parsedColor }
                                    viewModel.savePalette(projectId, paletteIndex, newColors)
                                    Toast.makeText(context, context.getString(R.string.color_pasted_successfully, cleanHex), Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, context.getString(R.string.error_parsing_hex, pastedText), Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, context.getString(R.string.invalid_hex_format), Toast.LENGTH_LONG).show()
                            }
                        },
                        showAddIcon = isTransparent
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}