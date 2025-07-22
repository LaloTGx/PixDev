package com.lalo.pixdev.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.luminance
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lalo.pixdev.ui.components.colorcomp.ContrastCheckerSection
import com.lalo.pixdev.ui.components.dialogs.HexColorInputDialog
import com.lalo.pixdev.viewmodel.ColorViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.lalo.pixdev.ui.components.generalscomp.ProjectHeader
import com.lalo.pixdev.viewmodel.model.Project
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.components.dialogs.ColorDialog
import com.lalo.pixdev.ui.components.sprites.SpriteImage
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.lalo.pixdev.ui.components.sprites.bouncyClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import com.lalo.pixdev.ui.components.colorcomp.ColorHarmoniesSection
import com.lalo.pixdev.ui.components.colorcomp.PalettePagerSection


@Composable
fun ColorsScreen(
    project: Project,
    navController: NavController,
    colorViewModel: ColorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val selectedTab = remember { mutableStateOf(0) }

    val tabs = listOf(
        context.getString(R.string.tab_harmonies),
        context.getString(R.string.tab_contrast),
        context.getString(R.string.tab_palettes)
    )

    val selectedColor by colorViewModel.selectedColor.collectAsStateWithLifecycle()
    val currentHue by colorViewModel.currentHue.collectAsStateWithLifecycle()
    val currentSaturation by colorViewModel.currentSaturation.collectAsStateWithLifecycle()
    val currentValue by colorViewModel.currentValue.collectAsStateWithLifecycle()

    var showColorDialog by remember { mutableStateOf(false) }
    val showHexInputDialog = remember { mutableStateOf(false) }

    val normalPixIconResId = R.drawable.pixicon
    val pressedPixIconResId = R.drawable.pixicon_press

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ProjectHeader(project = project, navController = navController)

            Row(
                modifier = Modifier.fillMaxWidth(). padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.Center){
                    Text(context.getString(R.string.primary_color_label), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                }
                Row (horizontalArrangement = Arrangement.End){
                    val colorDialogInteractionSource = remember { MutableInteractionSource() }
                    val colorDialogIsPressed by colorDialogInteractionSource.collectIsPressedAsState()
                    val currentColorDialogIconResId = if (colorDialogIsPressed) pressedPixIconResId else normalPixIconResId

                    SpriteImage(
                        drawableResId = currentColorDialogIconResId,
                        frameIndex = 4,
                        frameHeight = 64,
                        frameWidth = 64,
                        scale = 0.7f,
                        modifier = Modifier
                            .clip(RoundedCornerShape(0.dp))
                            .bouncyClickable(
                                onClick = { showColorDialog = true },
                                pressedScale = 0.9f,
                                pressedDarkenFactor = 0f,
                                unpressedScale = 1f,
                                interactionSource = colorDialogInteractionSource
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = selectedColor,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable { showHexInputDialog.value = true }
            ) {
                val hexColor = String.format("#%08X", selectedColor.toArgb())
                val red = (selectedColor.red * 255).toInt()
                val green = (selectedColor.green * 255).toInt()
                val blue = (selectedColor.blue * 255).toInt()
                Text(
                    text = "${context.getString(R.string.hex_label)} $hexColor ${context.getString(R.string.rgb_label)} ($red, $green, $blue)",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    color = if (selectedColor.luminance() > 0.5) Color.Black else Color.White
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            HueSaturationValuePicker(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(150.dp),
                currentHue = currentHue,
                currentSaturation = currentSaturation,
                currentValue = currentValue,
                onColorSelected = { saturation, value ->
                    colorViewModel.updateSaturationAndValue(saturation, value)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "${context.getString(R.string.hue_label)} %.0f°".format(currentHue),
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                color = MaterialTheme.colorScheme.onBackground)
            HueSlider(
                modifier = Modifier.fillMaxWidth().height(30.dp).padding(horizontal = 16.dp),
                currentHue = currentHue,
                onHueChanged = { newHue ->
                    colorViewModel.updateHue(newHue)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "${context.getString(R.string.opacity_label)} ${(selectedColor.alpha * 100).toInt()}%",
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                color = MaterialTheme.colorScheme.onBackground)
            OpacitySlider(
                modifier = Modifier.fillMaxWidth().height(30.dp).padding(horizontal = 16.dp),
                baseColor = Color.hsv(currentHue, currentSaturation, currentValue),
                currentAlpha = selectedColor.alpha,
                onAlphaChanged = { newAlpha ->
                    colorViewModel.updateAlpha(newAlpha)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (showHexInputDialog.value) {
                HexColorInputDialog(
                    initialColor = selectedColor,
                    onDismiss = { showHexInputDialog.value = false },
                    onColorConfirmed = { newColor ->
                        colorViewModel.updateSelectedColor(newColor)
                        showHexInputDialog.value = false
                    }
                )
            }

            TabRow(
                selectedTabIndex = selectedTab.value,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .border(6.dp, MaterialTheme.colorScheme.onBackground)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab.value == index

                    val animatedTabBackgroundColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background,
                        animationSpec = tween(durationMillis = 300),
                        label = "TabBackgroundColorAnimation"
                    )

                    val animatedTabTextColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                        animationSpec = tween(durationMillis = 300),
                        label = "TabTextColorAnimation"
                    )

                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab.value = index },
                        modifier = Modifier
                            .weight(1f)
                            .background(animatedTabBackgroundColor)
                    ) {
                        Text(
                            text = title,
                            color = animatedTabTextColor,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth()
                    .weight(0.5f)
                    .background(MaterialTheme.colorScheme.background)
                    .border(6.dp, MaterialTheme.colorScheme.onBackground)
                    .padding(top = 16.dp)
            ) {
                when (selectedTab.value) {
                    0 -> {
                        ColorHarmoniesSection(viewModel = colorViewModel, selectedColor = selectedColor)
                    }
                    1 -> {
                        ContrastCheckerSection(viewModel = colorViewModel, selectedColor = selectedColor)
                    }
                    2 -> {
                        PalettePagerSection(
                            viewModel = colorViewModel,
                            projectId = project.id,
                            onSelectColorFromPalette = { color ->
                                colorViewModel.updateSelectedColor(color)
                            }
                        )
                    }
                }
            }

            if (showColorDialog) {
                ColorDialog(onDismissRequest = { showColorDialog = false })
            }
        }
    }
}

@Composable
fun OpacitySlider(
    modifier: Modifier = Modifier,
    baseColor: Color,
    currentAlpha: Float,
    onAlphaChanged: (Float) -> Unit
) {
    Canvas(modifier = modifier.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset ->
                val newAlpha = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                onAlphaChanged(newAlpha)
            },
            onDrag = { change, _ ->
                val newAlpha = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                onAlphaChanged(newAlpha)
                change.consume()
            }
        )
    }) {
        val width = size.width
        val height = size.height

        val transparentColor = baseColor.copy(alpha = 0f)
        val opaqueColor = baseColor.copy(alpha = 1f)

        val alphaGradient = Brush.horizontalGradient(
            colors = listOf(transparentColor, opaqueColor)
        )
        drawRect(brush = alphaGradient, size = size)

        val indicatorX = currentAlpha * width
        drawCircle(
            color = Color.White,
            radius = 8.dp.toPx(),
            center = Offset(indicatorX, height / 2f),
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = Color.Black,
            radius = 6.dp.toPx(),
            center = Offset(indicatorX, height / 2f),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun HueSaturationValuePicker(
    modifier: Modifier = Modifier,
    currentHue: Float,
    currentSaturation: Float,
    currentValue: Float,
    onColorSelected: (saturation: Float, value: Float) -> Unit
) {
    Canvas(modifier = modifier.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset ->
                val width = size.width.toFloat()
                val height = size.height.toFloat()

                val saturation = (offset.x / width).coerceIn(0f, 1f)
                val value = (1f - (offset.y / height)).coerceIn(0f, 1f)

                onColorSelected(saturation, value)
            },
            onDrag = { change, _ ->
                val width = size.width.toFloat()
                val height = size.height.toFloat()
                val currentOffset = change.position

                val saturation = (currentOffset.x / width).coerceIn(0f, 1f)
                val value = (1f - (currentOffset.y / height)).coerceIn(0f, 1f)

                onColorSelected(saturation, value)
                change.consume()
            }
        )
    }) {
        val width = size.width
        val height = size.height

        val saturationBrush = Brush.horizontalGradient(
            colors = listOf(
                Color.hsv(currentHue, 0f, 1f),
                Color.hsv(currentHue, 1f, 1f)
            )
        )
        drawRect(brush = saturationBrush, size = size)

        val valueBrush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.Black)
        )
        drawRect(brush = valueBrush, size = size)

        val pointerX = currentSaturation * width
        val pointerY = (1f - currentValue) * height
        drawCircle(
            color = Color.White,
            radius = 8.dp.toPx(),
            center = Offset(pointerX, pointerY),
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = Color.Black,
            radius = 6.dp.toPx(),
            center = Offset(pointerX, height / 2f),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun HueSlider(
    modifier: Modifier = Modifier,
    currentHue: Float,
    onHueChanged: (Float) -> Unit
) {
    Canvas(modifier = modifier.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset ->
                val newHue = (offset.x / size.width.toFloat()) * 360f
                onHueChanged(newHue.coerceIn(0f, 360f))
            },
            onDrag = { change, _ ->
                val newHue = (change.position.x / size.width.toFloat()) * 360f
                onHueChanged(newHue.coerceIn(0f, 360f))
                change.consume()
            }
        )
    }) {
        val width = size.width

        val hueColors = listOf(
            Color.hsv(0f, 1f, 1f),
            Color.hsv(60f, 1f, 1f),
            Color.hsv(120f, 1f, 1f),
            Color.hsv(180f, 1f, 1f),
            Color.hsv(240f, 1f, 1f),
            Color.hsv(300f, 1f, 1f),
            Color.hsv(360f, 1f, 1f)
        )

        val colorStops = Array(hueColors.size) { i ->
            val fraction = i.toFloat() / (hueColors.size - 1).toFloat()
            fraction to hueColors[i]
        }

        val hueGradient = Brush.horizontalGradient(
            colorStops = colorStops.toList().toTypedArray()
        )
        drawRect(brush = hueGradient, size = size)

        val indicatorX = (currentHue / 360f) * width
        drawLine(
            color = Color.White,
            start = Offset(indicatorX, 0f),
            end = Offset(indicatorX, size.height),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color.Black,
            start = Offset(indicatorX + 1.dp.toPx(), 0f),
            end = Offset(indicatorX + 1.dp.toPx(), size.height),
            strokeWidth = 2.dp.toPx()
        )
    }
}