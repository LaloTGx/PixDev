package com.lalo.pixdev.ui.components.colorcomp

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.lalo.pixdev.R

@Composable
fun PaletteColorBlock(
    color: Color,
    modifier: Modifier = Modifier,
    onColorClick: () -> Unit,
    onColorLongPress: () -> Unit,
    onColorDoubleClickPaste: (pastedHex: String) -> Unit,
    showAddIcon: Boolean = false
) {
    val context = LocalContext.current

    Surface(
        color = if (showAddIcon) Color.Transparent else color,
        modifier = modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onColorClick,
                onLongClick = onColorLongPress,
                onDoubleClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = clipboard.primaryClip
                    if (clipData != null && clipData.itemCount > 0) {
                        val pastedText = clipData.getItemAt(0).text?.toString()
                        if (pastedText != null) {
                            onColorDoubleClickPaste(pastedText)
                        } else {
                            Toast.makeText(context, context.getString(R.string.clipboard_empty_or_not_text), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.clipboard_empty), Toast.LENGTH_SHORT).show()
                    }
                }
            ),
        border = if (showAddIcon) BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface) else null,
        shape = MaterialTheme.shapes.small
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (showAddIcon) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            } else {
                val hexColor = String.format("#%06X", color.toArgb() and 0xFFFFFF)
                Text(
                    text = hexColor,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (color.luminance() > 0.5) Color.Black else Color.White
                )
            }
        }
    }
}