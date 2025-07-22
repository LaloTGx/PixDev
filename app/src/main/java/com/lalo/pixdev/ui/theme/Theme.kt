package com.lalo.pixdev.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.LaunchedEffect // Importa LaunchedEffect

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = BackgroundDark,
    surface = BackgroundSurfaceD,
    onBackground = TextDark,
    onSurface = TextDark,
    error = red
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = BackgroundLight,
    surface = BackgroundSurfaceL,
    onBackground = TextLight,
    onSurface = TextLight,
    error = red
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PixDevTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val context = LocalContext.current

    // Cargar la preferencia de fuente al inicio del tema
    // Usa 'Unit' como clave para que se ejecute solo una vez cuando el composable entra en composición
    LaunchedEffect(Unit) {
        AppTypographyManager.loadFontPreference(context)
    }

    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val appTypography = AppTypography(windowSizeClass.widthSizeClass)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography, // Aquí se usa la tipografía dinámica que ahora obtiene la fuente del manager
        content = content
    )
}