package com.lalo.pixdev.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

fun tickerFlow(periodMillis: Long = 60000L) = flow {
    while (true) {
        emit(Unit)
        delay(periodMillis)
    }
}

@Composable
fun rememberTicker(tickerFlow: kotlinx.coroutines.flow.Flow<Unit>): Int {
    val recomposeTrigger = remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(tickerFlow) {
        tickerFlow.collect {
            recomposeTrigger.value++
        }
    }

    return recomposeTrigger.value
}
