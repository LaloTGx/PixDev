package com.lalo.pixdev.utils

import android.content.Context
import com.lalo.pixdev.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Long.toFriendlyTime(context: Context): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    val calendarNow = Calendar.getInstance().apply { timeInMillis = now }
    val calendarThen = Calendar.getInstance().apply { timeInMillis = this@toFriendlyTime }

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    val locale = context.resources.configuration.locales[0] ?: Locale.getDefault()

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> context.getString(R.string.just_now)
        minutes < 60 -> context.getString(R.string.minutes_ago, minutes)
        hours < 24 && calendarNow.get(Calendar.DAY_OF_YEAR) == calendarThen.get(Calendar.DAY_OF_YEAR) -> {
            context.getString(R.string.today_at, SimpleDateFormat("hh:mm a", locale).format(Date(this)))
        }
        (calendarNow.get(Calendar.DAY_OF_YEAR) - calendarThen.get(Calendar.DAY_OF_YEAR) == 1) ||
                (calendarNow.get(Calendar.DAY_OF_YEAR) == 1 && calendarThen.get(Calendar.DAY_OF_YEAR) >= 364) -> {
            context.getString(R.string.yesterday_at, SimpleDateFormat("hh:mm a", locale).format(Date(this)))
        }
        days < 7 -> {
            val dayOfWeek = SimpleDateFormat("EEEE", locale).format(Date(this)).replaceFirstChar { it.uppercase() }
            val timeOfDay = SimpleDateFormat("hh:mm a", locale).format(Date(this))
            context.getString(R.string.day_at, dayOfWeek, timeOfDay)
        }
        else -> {
            SimpleDateFormat("dd MMM yyyy", locale).format(Date(this))
        }
    }
}
