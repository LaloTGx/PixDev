package com.lalo.pixdev.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return format.format(date)
}

fun Long.toFriendlyTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    val calendarNow = Calendar.getInstance().apply { timeInMillis = now }
    val calendarThen = Calendar.getInstance().apply { timeInMillis = this@toFriendlyTime }

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Justo ahora"
        minutes < 60 -> "Hace $minutes minutos"
        hours < 24 && calendarNow.get(Calendar.DAY_OF_YEAR) == calendarThen.get(Calendar.DAY_OF_YEAR) -> {
            "Hoy a las ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(this))}"
        }
        (calendarNow.get(Calendar.DAY_OF_YEAR) - calendarThen.get(Calendar.DAY_OF_YEAR) == 1) ||
                (calendarNow.get(Calendar.DAY_OF_YEAR) == 1 && calendarThen.get(Calendar.DAY_OF_YEAR) >= 364) -> {
            "Ayer a las ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(this))}"
        }
        days < 7 -> {
            "${SimpleDateFormat("EEEE 'a las' hh:mm a", Locale.getDefault()).format(Date(this))}"
        }
        else -> {
            this.toFormattedDate()
        }
    }
}
