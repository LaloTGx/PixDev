package com.lalo.pixdev.ui.components.colorcomp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lalo.pixdev.R

@Composable
fun ColorBlock(color: Color, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val hexColor = String.format("#%08X", color.toArgb())

    Surface(
        color = color,
        modifier = modifier
            .combinedClickable(
                onClick = {},
                onLongClick = {},
                onDoubleClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Color Hex", hexColor)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, context.getString(R.string.color_copied_to_clipboard, hexColor), Toast.LENGTH_SHORT).show()
                }
            )
            .border(2.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        val displayHex = String.format("#%06X", color.toArgb() and 0xFFFFFF)
        Text(
            text = displayHex,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            color = if (color.luminance() > 0.5) Color.Black else Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}