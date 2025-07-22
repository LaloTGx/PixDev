package com.lalo.pixdev.ui.components.generalscomp

import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import com.lalo.pixdev.utils.toFriendlyTime
import kotlinx.coroutines.delay

@Composable
fun FriendlyTimeText(timestampMillis: Long, isParentPinned: Boolean = false) {
    val context = LocalContext.current

    val tick by produceState(initialValue = 0) {
        while (true) {
            delay(1000L)
            value = value + 1
        }
    }

    var friendlyTime by remember { mutableStateOf(timestampMillis.toFriendlyTime(context)) }
    LaunchedEffect(timestampMillis, tick) {
        friendlyTime = timestampMillis.toFriendlyTime(context)
    }

    val textColor = if (isParentPinned) MaterialTheme.colorScheme.background
    else MaterialTheme.colorScheme.onBackground

    Text(
        text = friendlyTime,
        style = MaterialTheme.typography.bodySmall,
        color = textColor
    )
}