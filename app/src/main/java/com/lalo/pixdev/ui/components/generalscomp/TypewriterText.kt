package com.lalo.pixdev.ui.components.generalscomp

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    delayBetweenCharsMillis: Long = 50L,
    onAnimationEnd: () -> Unit = {}
) {
    var displayedText by remember { mutableStateOf("") }
    var animationFinished by remember { mutableStateOf(false) }

    LaunchedEffect(text) {
        displayedText = ""
        animationFinished = false

        text.forEachIndexed { index, char ->
            displayedText = text.substring(0, index + 1)
            delay(delayBetweenCharsMillis)
        }
        animationFinished = true
        onAnimationEnd()
    }

    Text(
        text = displayedText,
        modifier = modifier,
        style = textStyle
    )
}