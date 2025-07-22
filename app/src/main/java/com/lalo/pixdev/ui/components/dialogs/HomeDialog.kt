package com.lalo.pixdev.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lalo.pixdev.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lalo.pixdev.ui.components.sprites.SpriteBackground
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.foundation.combinedClickable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import com.lalo.pixdev.ui.components.sprites.SpriteAnimation
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.text.style.TextAlign
import com.lalo.pixdev.ui.components.generalscomp.PageIndicators

@Composable
fun HomeDialog(onDismissRequest: () -> Unit) {
    val context = LocalContext.current
    var showContent by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        PageContent.SingleTap,
        PageContent.DoubleTap,
        PageContent.Swipe,
        PageContent.LongPress
    )

    val pagerState = rememberPagerState(initialPage = 0) {
        pages.size
    }

    LaunchedEffect(Unit) {
        showContent = true
    }

    Dialog(
        onDismissRequest = {
            coroutineScope.launch {
                showContent = false
                delay(300)
                onDismissRequest()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = 0.5f,
                    stiffness = 200f
                ),
                initialScale = 0.1f
            ),
            exit = scaleOut(
                animationSpec = spring(
                    dampingRatio = 0.5f,
                    stiffness = 200f
                ),
                targetScale = 0.1f
            )
        ) {
            SpriteBackground(
                drawableResId = R.drawable.spritedialog,
                frameIndex = 0,
                frameWidth = 64,
                frameHeight = 68,
                scale = 8f,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.home_dialog_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f)
                    ) { pageIndex ->
                        PageContent(pages[pageIndex])
                    }

                    // Opcional: Indicadores de página
                    Spacer(modifier = Modifier.height(8.dp))
                    PageIndicators(pagerState, pages.size)
                }
            }
        }
    }
}

sealed class PageContent(
    val titleResId: Int,
    val descriptionResId: Int,
    val spriteResId: Int,
    val frameCount: Int,
    val startFrame: Int,
    val endFrame: Int
) {
    object SingleTap : PageContent(
        R.string.home_dialog_single_tap_title,
        R.string.home_dialog_single_tap_description,
        R.drawable.handonclic,
        7, 0, 7
    )
    object DoubleTap : PageContent(
        R.string.home_dialog_double_tap_title,
        R.string.home_dialog_double_tap_description,
        R.drawable.handdoubleclic,
        10, 0, 10
    )
    object Swipe : PageContent(
        R.string.home_dialog_swipe_title,
        R.string.home_dialog_swipe_description,
        R.drawable.handswipe,
        23, 0, 23
    )
    object LongPress : PageContent(
        R.string.home_dialog_long_press_title,
        R.string.home_dialog_long_press_description,
        R.drawable.handlongclic,
        11, 0, 11
    )
}

@Composable
fun PageContent(page: PageContent) {
    Column(Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpriteAnimation(
                drawableResId = page.spriteResId,
                frameCount = page.frameCount,
                startFrame = page.startFrame,
                endFrame = page.endFrame,
                animationScale = 3f,
                animationDelayMillis = 200L,
                loop = true
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(Modifier.fillMaxWidth(), Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(page.titleResId),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = Bold
                )
                Text(
                    text = stringResource(page.descriptionResId),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}