 package com.lalo.pixdev.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lalo.pixdev.R
import com.lalo.pixdev.navigation.Routes
import com.lalo.pixdev.ui.components.RequirementItemCard
import com.lalo.pixdev.ui.components.dialogs.RequirementDialog
import com.lalo.pixdev.ui.components.generalscomp.ProjectHeader
import com.lalo.pixdev.ui.components.requerimentcomp.RequirementInputPanel
import com.lalo.pixdev.ui.components.sprites.SpriteImage
import com.lalo.pixdev.ui.components.sprites.bouncyClickable
import com.lalo.pixdev.ui.components.requerimentcomp.RequirementPriority
import com.lalo.pixdev.viewmodel.model.Prioridad

import com.lalo.pixdev.viewmodel.RequirementViewModel
import com.lalo.pixdev.viewmodel.model.Project
import com.lalo.pixdev.viewmodel.model.RequirementSortType
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

sealed class FilterPriorityState(val frameIndex: Int, val vmPriority: Prioridad?) {
    object All : FilterPriorityState(frameIndex = 12, vmPriority = null)
    object Optional : FilterPriorityState(frameIndex = RequirementPriority.OPTIONAL.getSpriteFrameIndex(), vmPriority = Prioridad.OPTIONAL)
    object Mandatory : FilterPriorityState(frameIndex = RequirementPriority.MANDATORY.getSpriteFrameIndex(), vmPriority = Prioridad.MANDATORY)
    object Essential : FilterPriorityState(frameIndex = RequirementPriority.ESSENTIAL.getSpriteFrameIndex(), vmPriority = Prioridad.ESSENTIAL)

    companion object {
        fun values(): List<FilterPriorityState> = listOf(All, Optional, Mandatory, Essential)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RequirementScreen(
    project: Project,
    navController: NavController,
    requirementViewModel: RequirementViewModel = hiltViewModel()
) {
    val selectedTab = remember { mutableStateOf(0) }
    val context = LocalContext.current

    val tabs = listOf(
        context.getString(R.string.tab_pending),
        context.getString(R.string.tab_completed)
    )

    val requirements by requirementViewModel.requirements.collectAsStateWithLifecycle()

    var showRequirementDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var expandedSortMenu by remember { mutableStateOf(false) }

    var currentFilterPriorityStateIndex by remember { mutableStateOf(0) }
    val filterPriorityStates = remember { FilterPriorityState.values() }

    val normalPixIconResId = R.drawable.pixicon
    val pressedPixIconResId = R.drawable.pixicon_press

    val density = LocalDensity.current

    val fullScreenHeightPx = remember { mutableStateOf(0f) }
    val projectHeaderHeightPx = remember { mutableStateOf(0f) }
    val inputPanelHeightPx = remember { mutableStateOf(0f) }

    val offsetY = remember { Animatable(0f) }
    val peekHeightDp = 280.dp

    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }

    LaunchedEffect(pagerState.currentPage) {
        selectedTab.value = pagerState.currentPage
    }

    LaunchedEffect(key1 = Unit) {
        val initialFilter = filterPriorityStates[currentFilterPriorityStateIndex]
        requirementViewModel.filterRequirementsByPriority(initialFilter.vmPriority)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .onSizeChanged { size ->
                    fullScreenHeightPx.value = size.height.toFloat()
                    if (offsetY.value == 0f && projectHeaderHeightPx.value != 0f) {
                        coroutineScope.launch {
                            offsetY.snapTo(fullScreenHeightPx.value - with(density) { peekHeightDp.toPx() })
                        }
                    }
                }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                ProjectHeader(
                    project = project,
                    navController = navController,
                    modifier = Modifier.onSizeChanged { size ->
                        projectHeaderHeightPx.value = size.height.toFloat()
                        if (offsetY.value == 0f && fullScreenHeightPx.value != 0f) {
                            coroutineScope.launch {
                                offsetY.snapTo(fullScreenHeightPx.value - with(density) { peekHeightDp.toPx() })
                            }
                        }
                    }
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    val reqDialogInteractionSource = remember { MutableInteractionSource() }
                    val reqDialogIsPressed by reqDialogInteractionSource.collectIsPressedAsState()
                    val currentReqDialogIconResId = if (reqDialogIsPressed) pressedPixIconResId else normalPixIconResId

                    SpriteImage(
                        drawableResId = currentReqDialogIconResId,
                        frameIndex = 4,
                        frameHeight = 64,
                        frameWidth = 64,
                        scale = 0.7f,
                        modifier = Modifier
                            .clip(RoundedCornerShape(0.dp))
                            .bouncyClickable(
                                onClick = { showRequirementDialog = true },
                                pressedScale = 0.9f,
                                pressedDarkenFactor = 0f,
                                unpressedScale = 1f,
                                interactionSource = reqDialogInteractionSource
                            )
                    )
                }

                RequirementInputPanel(
                    onAddRequirement = { name, priority, dateTimeMillis ->
                        requirementViewModel.addRequirement(
                            nombre = name,
                            prioridad = priority.name,
                            fechaLimite = dateTimeMillis
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { size ->
                            inputPanelHeightPx.value = size.height.toFloat()
                            if (offsetY.value == 0f && fullScreenHeightPx.value != 0f && projectHeaderHeightPx.value != 0f) {
                                coroutineScope.launch {
                                    offsetY.snapTo(fullScreenHeightPx.value - with(density) { peekHeightDp.toPx() })
                                }
                            }
                        }
                )
            }

            // - Bottom Sheet
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(x = 0, y = offsetY.value.roundToInt()) }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                coroutineScope.launch {
                                    val topBound = projectHeaderHeightPx.value + with(density) { (peekHeightDp - 280.dp).toPx() }
                                    val bottomBound = fullScreenHeightPx.value - with(density) { peekHeightDp.toPx() }

                                    if (offsetY.value < (topBound + bottomBound) / 2) {
                                        offsetY.animateTo(topBound, animationSpec = spring())
                                    } else {
                                        offsetY.animateTo(bottomBound, animationSpec = spring())
                                    }
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                val newOffset = offsetY.value + dragAmount
                                val clampedOffset = newOffset.coerceIn(projectHeaderHeightPx.value + with(density) { (peekHeightDp - 280.dp).toPx() }, fullScreenHeightPx.value - with(density) { peekHeightDp.toPx() })
                                offsetY.snapTo(clampedOffset)
                            }
                        }
                    }
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // - Bar Drag
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), RoundedCornerShape(0.dp))
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val priorityFilterInteractionSource = remember { MutableInteractionSource() }
                    val priorityFilterIsPressed by priorityFilterInteractionSource.collectIsPressedAsState()

                    val currentFilterPriorityFrameIndex = filterPriorityStates[currentFilterPriorityStateIndex].frameIndex

                    SpriteImage(
                        drawableResId = if (priorityFilterIsPressed) pressedPixIconResId else normalPixIconResId,
                        frameIndex = currentFilterPriorityFrameIndex,
                        frameHeight = 64,
                        frameWidth = 64,
                        scale = 0.7f,
                        modifier = Modifier
                            .clip(RoundedCornerShape(0.dp))
                            .bouncyClickable(
                                onClick = {
                                    currentFilterPriorityStateIndex = (currentFilterPriorityStateIndex + 1) % filterPriorityStates.size
                                    val selectedFilter = filterPriorityStates[currentFilterPriorityStateIndex]
                                    requirementViewModel.filterRequirementsByPriority(selectedFilter.vmPriority)
                                },
                                pressedScale = 0.9f,
                                pressedDarkenFactor = 0f,
                                unpressedScale = 1f,
                                interactionSource = priorityFilterInteractionSource
                            )
                    )
                    Spacer(Modifier.width(8.dp))

                    val filterInteractionSource = remember { MutableInteractionSource() }
                    val filterIsPressed by filterInteractionSource.collectIsPressedAsState()
                    val filterIconResId = if (filterIsPressed) pressedPixIconResId else normalPixIconResId

                    SpriteImage(
                        drawableResId = filterIconResId,
                        frameIndex = 2,
                        frameHeight = 64,
                        frameWidth = 64,
                        scale = 0.7f,
                        modifier = Modifier
                            .clip(RoundedCornerShape(0.dp))
                            .bouncyClickable(
                                onClick = { expandedSortMenu = true },
                                pressedScale = 0.9f,
                                pressedDarkenFactor = 0f,
                                unpressedScale = 1f,
                                interactionSource = filterInteractionSource
                            )
                    )

                    DropdownMenu(
                        expanded = expandedSortMenu,
                        onDismissRequest = { expandedSortMenu = false }
                    ) {
                        RequirementSortType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = type.titleResId)) },
                                onClick = {
                                    requirementViewModel.sortRequirementsBy(type)
                                    expandedSortMenu = false
                                }
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))

                    TabRow(
                        selectedTabIndex = selectedTab.value,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .border(6.dp, MaterialTheme.colorScheme.onBackground)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = selectedTab.value == index

                            val animatedTabBackgroundColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background,
                                animationSpec = tween(durationMillis = 300),
                                label = "TabBackgroundColorAnimation"
                            )

                            val animatedTabTextColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                                animationSpec = tween(durationMillis = 300),
                                label = "TabTextColorAnimation"
                            )

                            Tab(
                                selected = isSelected,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .background(animatedTabBackgroundColor)
                            ) {
                                Text(
                                    text = title,
                                    color = animatedTabTextColor,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    when (page) {
                        0 -> {
                            val pendingRequirements = requirements.filter { !it.completado }
                            if (pendingRequirements.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.Top,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = context.getString(R.string.no_pending_requirements),
                                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(
                                        top = 8.dp,
                                        bottom = 140.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(pendingRequirements, key = { it.id }) { req ->
                                        RequirementItemCard(
                                            item = req,
                                            onToggleCompleted = { requirementId, isChecked ->
                                                requirementViewModel.toggleCompletado(
                                                    requirementId,
                                                    isChecked
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        1 -> {
                            val completedRequirements = requirements.filter { it.completado }
                            if (completedRequirements.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.Top,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = context.getString(R.string.no_completed_requirements),
                                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(
                                        top = 8.dp,
                                        bottom = 140.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(completedRequirements, key = { it.id }) { req ->
                                        RequirementItemCard(
                                            item = req,
                                            onToggleCompleted = { requirementId, isChecked ->
                                                requirementViewModel.toggleCompletado(
                                                    requirementId,
                                                    isChecked
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showRequirementDialog) {
            RequirementDialog(onDismissRequest = { showRequirementDialog = false })
        }
    }
}