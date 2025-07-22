package com.lalo.pixdev.viewmodel.model

import androidx.compose.ui.graphics.Color

data class Palette(
    val id: Long = 0,
    val projectId: String,
    val colors: List<Color>,
    val paletteIndex: Int
)