package com.lalo.pixdev

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.navigation.compose.rememberNavController
import com.lalo.pixdev.navigation.AppNavGraph
import com.lalo.pixdev.ui.theme.PixDevTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import android.view.WindowManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context?) {
        val sharedPrefs = newBase?.getSharedPreferences("PixDevPrefs", Context.MODE_PRIVATE)
        val savedLanguageCode = sharedPrefs?.getString("app_language", Locale.getDefault().language)

        if (newBase != null && savedLanguageCode != null) {
            val appLocale = LocaleListCompat.forLanguageTags(savedLanguageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
            val config = Configuration(newBase.resources.configuration)
            config.setLocale(Locale(savedLanguageCode))
            super.attachBaseContext(newBase.createConfigurationContext(config))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
            navigationBarColor = android.graphics.Color.TRANSPARENT
        }

        setContent {
            val darkTheme = isSystemInDarkTheme()

            PixDevTheme(darkTheme = darkTheme) {
                val view = LocalView.current
                val currentWindow = (view.context as? ComponentActivity)?.window

                val useDarkIcons = MaterialTheme.colorScheme.surface.luminance() > 0.5f

                SideEffect {
                    currentWindow?.let { window ->
                        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = useDarkIcons
                        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = useDarkIcons
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}