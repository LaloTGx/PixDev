package com.lalo.pixdev.ui.utils

import androidx.compose.ui.graphics.Color

fun isValidHex(hex: String): Boolean {
    return hex.matches(Regex("^#?[0-9A-Fa-f]{6}$|^#?[0-9A-Fa-f]{8}$"))
}

fun parseHexToColor(hex: String): Color? {
    return try {
        val cleanHex = hex.removePrefix("#")
        val fullHex = when (cleanHex.length) {
            6 -> "FF$cleanHex"
            8 -> cleanHex
            else -> return null
        }
        val argb = fullHex.toLong(16).toInt()
        Color(argb)
    } catch (e: Exception) {
        null
    }
}