package com.lalo.pixdev.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.components.dialogs.AboutDialog
import java.util.Locale
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.lalo.pixdev.ui.components.sprites.SpriteImage
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.lalo.pixdev.ui.components.generalscomp.CustomPixelSwitch
import com.lalo.pixdev.ui.theme.AppTypographyManager

object CalendarPrefs {
    const val PREFS_NAME = "PixDevPrefs"
    const val KEY_FONT_FAMILY = "app_font_family"
}

@Composable
fun SettingScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(CalendarPrefs.PREFS_NAME, Context.MODE_PRIVATE) }

    var alwaysShowLongSplash by remember {
        mutableStateOf(prefs.getBoolean("always_show_long_splash", false))
    }

    var showAboutDialog by remember { mutableStateOf(false) }

    val currentAppLocaleCode = AppCompatDelegate.getApplicationLocales().get(0)?.language ?: Locale.getDefault().language

    var selectedLanguageCode by remember {
        mutableStateOf(prefs.getString("app_language", currentAppLocaleCode) ?: "en")
    }
    var expandedLanguageMenu by remember { mutableStateOf(false) }

    var showLanguageChangeMessage by remember { mutableStateOf(false) }

    val availableLanguages = mapOf(
        "es" to context.getString(R.string.spanish),
        "en" to context.getString(R.string.english),
        //"pt" to context.getString(R.string.portuguese)
    )

    val languageFlagIndexes = mapOf(
        "es" to 0, // México
        "en" to 1, // USA
        //"pt" to 2  // Portugal/Brasil
    )

    var selectedFontKey by remember {
        mutableStateOf(prefs.getString(CalendarPrefs.KEY_FONT_FAMILY, AppTypographyManager.FONT_PIXELIFIED) ?: AppTypographyManager.FONT_PIXELIFIED)
    }
    var expandedFontMenu by remember { mutableStateOf(false) }

    val fontOptions = remember {
        mapOf(
            AppTypographyManager.FONT_PIXELIFIED to "Pixelified Sans",
            AppTypographyManager.FONT_DEFAULT to "Sistema (Default)",
            AppTypographyManager.FONT_MONTSERRAT to "Montserrat"
        )
    }

    // - Start app
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }
                Text(
                    text = context.getString(R.string.settings),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = context.getString(R.string.app_start_title),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = if (alwaysShowLongSplash) context.getString(R.string.always_full_intro) else context.getString(R.string.short_after_first),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    CustomPixelSwitch(
                        checked = alwaysShowLongSplash,
                        onCheckedChange = { isChecked ->
                            alwaysShowLongSplash = isChecked
                            prefs.edit().putBoolean("always_show_long_splash", isChecked).apply()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedLanguageMenu = true }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = context.getString(R.string.language),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = availableLanguages[selectedLanguageCode] ?: context.getString(R.string.english),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    AnimatedVisibility(
                        visible = showLanguageChangeMessage,
                        enter = expandVertically(expandFrom = Alignment.Top),
                        exit = shrinkVertically(shrinkTowards = Alignment.Top)
                    ) {
                        Text(
                            text = context.getString(R.string.language_change_info),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expandedLanguageMenu,
                        onDismissRequest = { expandedLanguageMenu = false }
                    ) {
                        availableLanguages.forEach { (code, name) ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val flagIndex = languageFlagIndexes[code]
                                        if (flagIndex != null) {
                                            SpriteImage(
                                                drawableResId = R.drawable.flags,
                                                frameIndex = flagIndex,
                                                frameHeight = 15,
                                                frameWidth = 25,
                                                scale = 1f,
                                                modifier = Modifier
                                                    .padding(end = 8.dp)
                                                    .clip(RoundedCornerShape(0.dp))
                                            )
                                        }
                                        Text(name)
                                    }
                                },
                                onClick = {
                                    if (selectedLanguageCode != code) {
                                        selectedLanguageCode = code
                                        prefs.edit().putString("app_language", code).apply()

                                        val appLocale = LocaleListCompat.forLanguageTags(code)
                                        AppCompatDelegate.setApplicationLocales(appLocale)

                                        showLanguageChangeMessage = true
                                    }
                                    expandedLanguageMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Selector de Fuente
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedFontMenu = true }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Fuente de la aplicación",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = fontOptions[selectedFontKey] ?: "Pixelified Sans",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    DropdownMenu(
                        expanded = expandedFontMenu,
                        onDismissRequest = { expandedFontMenu = false }
                    ) {
                        fontOptions.forEach { (key, name) ->
                            DropdownMenuItem(
                                text = { Text(name, fontFamily = AppTypographyManager.getFontFamily(key)) },
                                onClick = {
                                    if (selectedFontKey != key) {
                                        selectedFontKey = key
                                        prefs.edit().putString(CalendarPrefs.KEY_FONT_FAMILY, key).apply()
                                        AppTypographyManager.notifyFontChange(context)
                                    }
                                    expandedFontMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = context.getString(R.string.app_version),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { showAboutDialog = true }
                ) {
                    SpriteImage(
                        drawableResId = R.drawable.logomov,
                        frameIndex = 44,
                        frameHeight = 64,
                        frameWidth = 64,
                        scale = 2f,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Lalo :]",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = { showAboutDialog = false })
    }
}