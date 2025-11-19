package com.lalo.pixdev.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lalo.pixdev.viewmodel.model.Project
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import com.lalo.pixdev.R
import com.lalo.pixdev.ui.components.generalscomp.FriendlyTimeText

@Composable
fun ProjectItem(
    project: Project,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onDoubleClick: (() -> Unit)? = null,
    completionPercentage: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val animatedProgress by animateFloatAsState(
        targetValue = completionPercentage / 100f,
        animationSpec = tween(durationMillis = 500), label = "animatedProgress"
    )

    val fillProgress by animateFloatAsState(
        targetValue = if (project.isPinned) 1f else 0f,
        animationSpec = tween(durationMillis = 400), label = "fillProgress"
    )

    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    val contentTextColor = if (project.isPinned) backgroundColor else onBackgroundColor
    val pinnedTextColor = if (project.isPinned) backgroundColor else onBackgroundColor

    val progressBarColor = if (project.isPinned) backgroundColor else onBackgroundColor
    val progressBarTrackColor = if (project.isPinned) onBackgroundColor else backgroundColor


    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(backgroundColor)
            .drawBehind {
                if (project.isPinned || fillProgress > 0f) {
                    drawRect(
                        color = onBackgroundColor,
                        topLeft = Offset(0f, size.height * (1f - fillProgress)),
                        size = Size(size.width, size.height * fillProgress)
                    )
                }
            }
            .border(3.dp, onBackgroundColor, RoundedCornerShape(3.dp))
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick
            )
            .padding(vertical = 18.dp, horizontal = 25.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentTextColor
                )
                if (project.isPinned) {
                    Text(
                        text = context.getString(R.string.pinned_indicator),
                        style = MaterialTheme.typography.labelSmall,
                        color = pinnedTextColor
                    )
                } else {
                    Spacer(modifier = Modifier.width(0.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = {animatedProgress},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                color = progressBarColor,
                trackColor = progressBarTrackColor,
                strokeCap = StrokeCap.Butt
            )
            Text(
                text = "$completionPercentage%",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.End),
                color = contentTextColor
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FriendlyTimeText(timestampMillis = project.dateCreated, isParentPinned = project.isPinned)
            }
        }
    }
}