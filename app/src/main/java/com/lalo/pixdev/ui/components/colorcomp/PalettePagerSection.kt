package com.lalo.pixdev.ui.components.colorcomp

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.components.generalscomp.PageIndicators
import com.lalo.pixdev.ui.utils.isValidHex
import com.lalo.pixdev.ui.utils.parseHexToColor
import com.lalo.pixdev.viewmodel.ColorViewModel

@Composable
fun PalettePagerSection(
    viewModel: ColorViewModel,
    projectId: String,
    onSelectColorFromPalette: (color: Color) -> Unit,
) {
    val context = LocalContext.current
    val palettes by viewModel.palettes.collectAsStateWithLifecycle()
    val selectedColor by viewModel.selectedColor.collectAsStateWithLifecycle()

    LaunchedEffect(projectId) {
        viewModel.loadPalettesForProject(projectId)
    }

    val pagerState = rememberPagerState(initialPage = 0) {
        palettes.size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { pageIndex ->
            val paletteColorsFlow = palettes.getOrNull(pageIndex)
            val paletteColors = paletteColorsFlow?.collectAsStateWithLifecycle()?.value ?: emptyList()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = context.getString(R.string.palette_number, pageIndex + 1),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

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
                                    viewModel.savePalette(projectId, pageIndex, newColors)
                                } else {
                                    onSelectColorFromPalette(color)
                                }
                            },
                            onColorLongPress = {
                                val newColors = paletteColors.toMutableList().apply { this[colorIndex] = selectedColor }
                                viewModel.savePalette(projectId, pageIndex, newColors)
                            },
                            onColorDoubleClickPaste = { pastedText ->
                                val cleanHex = pastedText.removePrefix("#").uppercase()

                                if (isValidHex(cleanHex)) {
                                    val parsedColor = parseHexToColor(cleanHex)
                                    if (parsedColor != null) {
                                        val newColors = paletteColors.toMutableList().apply { this[colorIndex] = parsedColor }
                                        viewModel.savePalette(projectId, pageIndex, newColors)
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

        Spacer(modifier = Modifier.height(8.dp))
        if (palettes.size > 1) {
            PageIndicators(pagerState = pagerState, pageCount = palettes.size)
        }
    }
}