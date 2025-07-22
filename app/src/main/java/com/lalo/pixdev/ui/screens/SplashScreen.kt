package com.lalo.pixdev.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.unit.dp
import com.lalo.pixdev.MainActivity
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.theme.PixDevTheme
import com.lalo.pixdev.ui.components.sprites.FullSpriteAnimation

class SplashScreenActivity : ComponentActivity() {

    private val PREFS_NAME = "PixDevPrefs"
    private val KEY_FIRST_LAUNCH = "is_first_launch"
    private val KEY_ALWAYS_SHOW_LONG_SPLASH = "always_show_long_splash"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean(KEY_FIRST_LAUNCH, true)
        val alwaysShowLongSplash = sharedPrefs.getBoolean(KEY_ALWAYS_SHOW_LONG_SPLASH, false)

        setContent {
            PixDevTheme {
                if (alwaysShowLongSplash) {
                    FullSpriteAnimation(
                        drawableResId = R.drawable.logomov,
                        frameCount = 130,
                        startFrame = 0,
                        endFrame = 129,
                        animationScale = 8f,
                        animationAreaWidth = 300.dp,
                        animationAreaHeight = 300.dp,
                        animationDelayMillis = 70L,
                        loop = false,
                        onAnimationEnd = { navigateToMain() }
                    )
                } else if (isFirstLaunch) {
                    FullSpriteAnimation(
                        drawableResId = R.drawable.logomov,
                        frameCount = 130,
                        startFrame = 0,
                        endFrame = 129,
                        animationScale = 8f,
                        animationAreaWidth = 300.dp,
                        animationAreaHeight = 300.dp,
                        animationDelayMillis = 70L,
                        loop = false,
                        onAnimationEnd = {
                            sharedPrefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
                            navigateToMain()
                        }
                    )
                } else {
                    FullSpriteAnimation(
                        drawableResId = R.drawable.logomov,
                        frameCount = 130,
                        startFrame = 0,
                        endFrame = 47,
                        animationScale = 8f,
                        animationAreaWidth = 300.dp,
                        animationAreaHeight = 300.dp,
                        animationDelayMillis = 60L,
                        loop = false,
                        onAnimationEnd = { navigateToMain() }
                    )
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}