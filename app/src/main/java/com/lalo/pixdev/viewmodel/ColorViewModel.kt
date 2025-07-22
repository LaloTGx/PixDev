package com.lalo.pixdev.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import android.graphics.Color as AndroidColor
import com.lalo.pixdev.data.local.repository.ColorRepository
import com.lalo.pixdev.viewmodel.model.Palette
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class ColorViewModel @Inject constructor(
    private val colorRepository: ColorRepository
) : ViewModel() {

    private val _selectedColor = MutableStateFlow(Color.Red)
    val selectedColor: StateFlow<Color> = _selectedColor.asStateFlow()

    private val _contrastColor = MutableStateFlow(Color.White)
    val contrastColor: StateFlow<Color> = _contrastColor.asStateFlow()

    private val _isColorPrimaryForeground = MutableStateFlow(true)
    val isColorPrimaryForeground: StateFlow<Boolean> = _isColorPrimaryForeground.asStateFlow()

    private val _currentHue = MutableStateFlow(0f)
    val currentHue: StateFlow<Float> = _currentHue.asStateFlow()

    private val _currentSaturation = MutableStateFlow(1f)
    val currentSaturation: StateFlow<Float> = _currentSaturation.asStateFlow()

    private val _currentValue = MutableStateFlow(1f)
    val currentValue: StateFlow<Float> = _currentValue.asStateFlow()

    private val _palettes = MutableStateFlow(
        List(3) {
            MutableStateFlow(List(5) { Color.Transparent })
        }
    )
    val palettes: StateFlow<List<StateFlow<List<Color>>>> = _palettes.asStateFlow()

    init {
        _selectedColor.value.let {
            val hsv = FloatArray(3)
            AndroidColor.colorToHSV(it.toArgb(), hsv)
            _currentHue.value = hsv[0]
            _currentSaturation.value = hsv[1]
            _currentValue.value = hsv[2]
        }
    }

    fun updateSelectedColor(newColor: Color) {
        _selectedColor.value = newColor
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(newColor.toArgb(), hsv)
        if (hsv[1] > 0.01f && hsv[2] > 0.01f) {
            _currentHue.value = hsv[0]
        }
        _currentSaturation.value = hsv[1]
        _currentValue.value = hsv[2]
    }

    fun updateHue(newHue: Float) {
        _currentHue.value = newHue
        _selectedColor.value = Color.hsv(newHue, _currentSaturation.value, _currentValue.value, _selectedColor.value.alpha)
    }

    fun updateSaturationAndValue(newSaturation: Float, newValue: Float) {
        _currentSaturation.value = newSaturation
        _currentValue.value = newValue
        _selectedColor.value = Color.hsv(_currentHue.value, newSaturation, newValue, _selectedColor.value.alpha)
    }

    fun updateAlpha(newAlpha: Float) {
        _selectedColor.value = _selectedColor.value.copy(alpha = newAlpha)
    }

    fun updateContrastColor(newColor: Color) {
        _contrastColor.value = newColor
    }

    fun toggleIsColorPrimaryForeground() {
        _isColorPrimaryForeground.value = !_isColorPrimaryForeground.value
    }

    fun loadPalettesForProject(projectId: String) {
        viewModelScope.launch {
            colorRepository.getPalettesForProject(projectId).collectLatest { loadedPalettes ->
                val currentPalettes = _palettes.value.toMutableList()
                loadedPalettes.forEach { palette ->
                    if (palette.paletteIndex in 0..2) {
                        currentPalettes[palette.paletteIndex].value = palette.colors
                    }
                }
                _palettes.value = currentPalettes
            }
        }
    }

    fun savePalette(projectId: String, paletteIndex: Int, colors: List<Color>) {
        viewModelScope.launch {
            if (colors.size == 5) {
                val paletteToSave = Palette(
                    projectId = projectId,
                    colors = colors,
                    paletteIndex = paletteIndex
                )
                colorRepository.savePalette(paletteToSave)
                _palettes.value[paletteIndex].value = colors
            }
        }
    }

    fun clearAndSavePalette(projectId: String, paletteIndex: Int) {
        viewModelScope.launch {
            colorRepository.deletePalette(projectId, paletteIndex)

            val transparentColors = List(5) { Color.Transparent }
            val emptyPaletteToSave = Palette(
                projectId = projectId,
                colors = transparentColors,
                paletteIndex = paletteIndex
            )
            colorRepository.savePalette(emptyPaletteToSave)

            _palettes.value[paletteIndex].value = transparentColors
        }
    }

    data class ContrastResult(
        val ratio: Float,
        val description: String,
        val meetsAA: Boolean,
        val meetsAALarge: Boolean,
        val meetsAAA: Boolean
    )

    fun checkContrast(color1: Color, color2: Color): ContrastResult {
        val L1 = color1.luminance()
        val L2 = color2.luminance()

        val brightest = maxOf(L1, L2)
        val darkest = minOf(L1, L2)

        val ratio = (brightest + 0.05f) / (darkest + 0.05f)

        val meetsAA = ratio >= 4.5f
        val meetsAALarge = ratio >= 3.0f
        val meetsAAA = ratio >= 7.0f

        val description = when {
            ratio >= 7.0f -> "Excelente (AAA)"
            ratio >= 4.5f -> "Aceptable (AA)"
            ratio >= 3.0f -> "Mínimo (AA Grande)"
            else -> "Pobre"
        }
        return ContrastResult(ratio, description, meetsAA, meetsAALarge, meetsAAA)
    }

    fun getComplementaryColor(color: Color): Color {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(color.toArgb(), hsv)
        val hue = (hsv[0] + 180) % 360
        return Color.hsv(hue, hsv[1], hsv[2], color.alpha)
    }

    fun getTriadicColors(color: Color): List<Color> {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(color.toArgb(), hsv)
        val hue1 = (hsv[0] + 120) % 360
        val hue2 = (hsv[0] + 240) % 360

        val sBase = hsv[1]
        val vBase = hsv[2]

        val color1 = Color.hsv(hue1, sBase, vBase, color.alpha)
        val s2 = (sBase * 0.9f).coerceIn(0.1f, 1f)
        val v2 = (vBase * 1.05f).coerceIn(0.1f, 1f)
        val color2 = Color.hsv(hue2, s2, v2, color.alpha)

        return listOf(color1, color2)
    }

    fun getAnalogousColors(color: Color): List<Color> {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(color.toArgb(), hsv)
        val hue1 = (hsv[0] + 30) % 360
        val hue2 = (hsv[0] - 30 + 360) % 360

        val sBase = hsv[1]
        val vBase = hsv[2]

        val color1 = Color.hsv(hue1, sBase, (vBase * 0.95f).coerceIn(0.1f, 1f), color.alpha)
        val color2 = Color.hsv(hue2, (sBase * 1.05f).coerceIn(0.1f, 1f), vBase, color.alpha)

        return listOf(color2, color1)
    }

    fun getMonochromaticColors(color: Color): List<Color> {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(color.toArgb(), hsv)
        val hue = hsv[0]
        val s = hsv[1]
        val v = hsv[2]

        return listOf(
            Color.hsv(hue, s, (v * 0.7f).coerceIn(0.1f, 1f), color.alpha),
            Color.hsv(hue, (s * 0.8f).coerceIn(0.1f, 1f), v, color.alpha),
            Color.hsv(hue, (s * 1.2f).coerceAtMost(1f), v, color.alpha),
            Color.hsv(hue, s, (v * 1.3f).coerceAtMost(1f), color.alpha)
        )
    }

    fun getCompoundColors(color: Color): List<Color> {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(color.toArgb(), hsv)
        val hue = hsv[0]
        val s = hsv[1]
        val v = hsv[2]

        val analogous1Hue = (hue + 30) % 360
        val analogous2Hue = (hue - 30 + 360) % 360
        val complementaryHue = (hue + 180) % 360

        return listOf(
            Color.hsv(analogous2Hue, s, v, color.alpha),
            Color.hsv(analogous1Hue, s, v, color.alpha),
            Color.hsv(complementaryHue, s, v, color.alpha)
        )
    }
}