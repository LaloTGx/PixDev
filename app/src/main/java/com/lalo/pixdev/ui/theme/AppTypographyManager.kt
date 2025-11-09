package com.lalo.pixdev.ui.theme

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.screens.settings.CalendarPrefs

// Definimos TODAS las fuentes personalizadas AQUI
val pixelFamily = FontFamily(
    Font(R.font.pixelifysans_bold, FontWeight.Bold),
    Font(R.font.pixelifysans_medium, FontWeight.Medium),
    Font(R.font.pixelifysans_regular, FontWeight.Normal),
    Font(R.font.pixelifysans_semibold, FontWeight.SemiBold)
)

val montserratFamily = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_bold, FontWeight.Bold)
)

val robotoFamily = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold)
)

// Objeto singleton para manejar la fuente seleccionada y notificar cambios
object AppTypographyManager {
    // Claves para identificar las fuentes
    const val FONT_PIXELIFIED = "pixelified"
    const val FONT_DEFAULT = "default" // Para la fuente por defecto de Compose/System
    const val FONT_MONTSERRAT = "montserrat"
    const val FONT_ROBOTO = "roboto"

    // Estado observable para la fuente actual. MutableState facilita la recomposición.
    // Inicialmente, usa tu fuente pixelada.
    var currentFontFamily = mutableStateOf(pixelFamily)

    fun loadFontPreference(context: Context) {
        val prefs = context.getSharedPreferences(CalendarPrefs.PREFS_NAME, Context.MODE_PRIVATE)
        val savedFontKey = prefs.getString(CalendarPrefs.KEY_FONT_FAMILY, FONT_PIXELIFIED) ?: FONT_PIXELIFIED
        currentFontFamily.value = getFontFamily(savedFontKey)
    }

    fun getFontFamily(fontKey: String): FontFamily {
        return when (fontKey) {
            FONT_PIXELIFIED -> pixelFamily
            FONT_MONTSERRAT -> montserratFamily
            FONT_ROBOTO -> robotoFamily
            FONT_DEFAULT -> FontFamily.Default // La fuente predeterminada del sistema
            else -> pixelFamily // Fallback a tu fuente pixelada
        }
    }

    fun notifyFontChange(context: Context) {
        loadFontPreference(context) // Vuelve a cargar la preferencia para actualizar el estado
    }
}