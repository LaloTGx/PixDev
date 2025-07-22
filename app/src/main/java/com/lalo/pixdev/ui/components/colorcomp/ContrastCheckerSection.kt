package com.lalo.pixdev.ui.components.colorcomp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.components.dialogs.HexColorInputDialog
import com.lalo.pixdev.ui.components.sprites.SpriteImage
import com.lalo.pixdev.viewmodel.ColorViewModel
import androidx.compose.ui.platform.LocalContext
import com.lalo.pixdev.ui.components.sprites.bouncyClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.text.style.TextAlign
import com.lalo.pixdev.ui.components.generalscomp.PageIndicators

@Composable
fun ContrastCheckerSection(
    viewModel: ColorViewModel,
    selectedColor: Color
) {
    val context = LocalContext.current
    val contrastColor by viewModel.contrastColor.collectAsStateWithLifecycle()
    val isColorPrimaryForeground by viewModel.isColorPrimaryForeground.collectAsStateWithLifecycle()

    val foregroundColor = remember(selectedColor, contrastColor, isColorPrimaryForeground) {
        if (isColorPrimaryForeground) selectedColor else contrastColor
    }
    val backgroundColor = remember(selectedColor, contrastColor, isColorPrimaryForeground) {
        if (isColorPrimaryForeground) contrastColor else selectedColor
    }

    val finalContrastResult = viewModel.checkContrast(foregroundColor, backgroundColor)

    val showContrastColorInputDialog = remember { mutableStateOf(false) }

    val normalPixIconResId = R.drawable.pixicon
    val pressedPixIconResId = R.drawable.pixicon_press

    val pageCount = 2
    val pagerState = rememberPagerState(initialPage = 0) {
        pageCount
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(vertical = 10.dp)) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { pageIndex ->
            when (pageIndex) {
                0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(context.getString(R.string.secondary_color_label), style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = contrastColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .clickable { showContrastColorInputDialog.value = true }
                        ) {
                            val hexContrast = String.format("#%08X", contrastColor.toArgb())
                            val red = (contrastColor.red * 255).toInt()
                            val green = (contrastColor.green * 255).toInt()
                            val blue = (contrastColor.blue * 255).toInt()
                            Text(
                                text = "${context.getString(R.string.hex_label)} $hexContrast ${context.getString(R.string.rgb_label)} ($red, $green, $blue)",
                                modifier = Modifier.fillMaxSize()
                                    .wrapContentSize(Alignment.Center),
                                color = if (contrastColor.luminance() > 0.5) Color.Black else Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val updateContrastInteractionSource = remember { MutableInteractionSource() }
                            val updateContrastIsPressed by updateContrastInteractionSource.collectIsPressedAsState()
                            val currentUpdateContrastIconResId = if (updateContrastIsPressed) pressedPixIconResId else normalPixIconResId

                            SpriteImage(
                                drawableResId = currentUpdateContrastIconResId,
                                frameIndex = 8,
                                frameHeight = 64,
                                frameWidth = 64,
                                scale = 0.7f,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(0.dp))
                                    .bouncyClickable(
                                        onClick = { viewModel.updateContrastColor(selectedColor) },
                                        pressedScale = 0.9f,
                                        pressedDarkenFactor = 0f,
                                        unpressedScale = 1f,
                                        interactionSource = updateContrastInteractionSource
                                    )
                            )

                            val toggleForegroundInteractionSource = remember { MutableInteractionSource() }
                            val toggleForegroundIsPressed by toggleForegroundInteractionSource.collectIsPressedAsState()
                            val currentToggleForegroundIconResId = if (toggleForegroundIsPressed) pressedPixIconResId else normalPixIconResId

                            SpriteImage(
                                drawableResId = currentToggleForegroundIconResId,
                                frameIndex = 7,
                                frameHeight = 64,
                                frameWidth = 64,
                                scale = 0.7f,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(0.dp))
                                    .bouncyClickable(
                                        onClick = { viewModel.toggleIsColorPrimaryForeground() },
                                        pressedScale = 0.9f,
                                        pressedDarkenFactor = 0f,
                                        unpressedScale = 1f,
                                        interactionSource = toggleForegroundInteractionSource
                                    )
                            )

                            Surface(
                                color = backgroundColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            ) {
                                Text(
                                    text = context.getString(R.string.example_text),
                                    color = foregroundColor,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.wrapContentSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${context.getString(R.string.ratio_label)} %.2f".format(finalContrastResult.ratio),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            val translatedDescription = when (finalContrastResult.description.substringBefore(" (").trim()) {
                                "Excelente" -> context.getString(R.string.contrast_excellent)
                                "Aceptable" -> context.getString(R.string.contrast_acceptable)
                                "Mínimo" -> context.getString(R.string.contrast_minimum)
                                "Pobre" -> context.getString(R.string.contrast_poor)
                                else -> finalContrastResult.description
                            }

                            Text(
                                text = "${context.getString(R.string.rating_label)} $translatedDescription",
                                style = MaterialTheme.typography.titleSmall,
                                color = when (translatedDescription) {
                                    context.getString(R.string.contrast_excellent) -> Color(0xFF4CAF50)
                                    context.getString(R.string.contrast_acceptable) -> Color(0xFFFFC107)
                                    else -> Color(0xFFF44336)
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = backgroundColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            Text(
                                text = context.getString(R.string.example_text),
                                color = foregroundColor,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.wrapContentSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${context.getString(R.string.ratio_label)} %.2f".format(finalContrastResult.ratio),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Divider(modifier = Modifier.fillMaxWidth().height(1.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(4.dp))

                        ContrastCriterionRow(
                            criterionText = context.getString(R.string.criterion_aa_large),
                            meetsCriterion = finalContrastResult.meetsAALarge
                        )
                        ContrastCriterionRow(
                            criterionText = context.getString(R.string.criterion_aa_normal),
                            meetsCriterion = finalContrastResult.meetsAA
                        )
                        ContrastCriterionRow(
                            criterionText = context.getString(R.string.criterion_aaa_enhanced),
                            meetsCriterion = finalContrastResult.meetsAAA
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        PageIndicators(pagerState = pagerState, pageCount = pageCount)
    }

    if (showContrastColorInputDialog.value) {
        HexColorInputDialog(
            initialColor = contrastColor,
            onDismiss = { showContrastColorInputDialog.value = false },
            onColorConfirmed = { newColor ->
                viewModel.updateContrastColor(newColor)
                showContrastColorInputDialog.value = false
            }
        )
    }
}

@Composable
fun ContrastCriterionRow(criterionText: String, meetsCriterion: Boolean) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = criterionText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (meetsCriterion) context.getString(R.string.meets_criterion_true) else context.getString(R.string.meets_criterion_false),
            style = MaterialTheme.typography.bodyMedium,
            color = if (meetsCriterion) Color(0xFF4CAF50) else Color(0xFFF44336)
        )
    }
}