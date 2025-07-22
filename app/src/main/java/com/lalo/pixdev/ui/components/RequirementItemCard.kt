package com.lalo.pixdev.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.components.requerimentcomp.RequirementPriority
import com.lalo.pixdev.ui.components.sprites.SpriteImage
import com.lalo.pixdev.viewmodel.model.Prioridad
import com.lalo.pixdev.viewmodel.model.Requirement
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun RequirementItemCard(
    item: Requirement,
    modifier: Modifier = Modifier,
    onToggleCompleted: (String, Boolean) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    val backgroundColor = if (item.completado) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background
    val borderColor = if (item.completado) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
    val textColor = if (item.completado) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
    val pixIconResId = R.drawable.pixicon

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .background(backgroundColor)
            .border(6.dp, borderColor)
            .clickable { onToggleCompleted(item.id, !item.completado) }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.completado,
                onCheckedChange = { isChecked ->
                    onToggleCompleted(item.id, isChecked)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = if (item.completado) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onBackground,
                    checkmarkColor = if (item.completado) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )

                val date = Date(item.fechaLimite)
                val formattedTime = timeFormatter.format(date)

                val calendarToday = Calendar.getInstance()
                val calendarItem = Calendar.getInstance().apply { timeInMillis = item.fechaLimite }

                val displayDateText = when {
                    calendarToday.get(Calendar.YEAR) == calendarItem.get(Calendar.YEAR) &&
                            calendarToday.get(Calendar.DAY_OF_YEAR) == calendarItem.get(Calendar.DAY_OF_YEAR) -> context.getString(R.string.date_today)
                    calendarToday.get(Calendar.YEAR) == calendarItem.get(Calendar.YEAR) &&
                            calendarToday.get(Calendar.DAY_OF_YEAR) + 1 == calendarItem.get(Calendar.DAY_OF_YEAR) -> context.getString(R.string.date_tomorrow)
                    else -> dateFormatter.format(date)
                }

                Text(
                    text = "${context.getString(R.string.date_label)} $displayDateText ${context.getString(R.string.at_time_label)} $formattedTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                val spritePriorityEnum = try {
                    RequirementPriority.valueOf(item.prioridad.uppercase(Locale.getDefault()))
                } catch (e: IllegalArgumentException) {
                    RequirementPriority.OPTIONAL
                }

                SpriteImage(
                    drawableResId = pixIconResId,
                    frameIndex = spritePriorityEnum.getSpriteFrameIndex(),
                    frameHeight = 64,
                    frameWidth = 64,
                    scale = 0.5f,
                    modifier = Modifier.size(32.dp)
                )

                val translatedPriority = try {
                    val prioridadEnum = Prioridad.valueOf(item.prioridad.uppercase(Locale.getDefault()))
                    context.getString(prioridadEnum.titleResId)
                } catch (e: IllegalArgumentException) {
                    item.prioridad
                }
                Text(
                    text = translatedPriority,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
        }
    }
}