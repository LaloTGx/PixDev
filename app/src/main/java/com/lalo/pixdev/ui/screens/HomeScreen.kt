package com.lalo.pixdev.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lalo.pixdev.R
import com.lalo.pixdev.navigation.Routes
import com.lalo.pixdev.ui.components.dialogs.AddProjectDialog
import com.lalo.pixdev.ui.components.dialogs.DeleteProjectConfirmationDialog
import com.lalo.pixdev.ui.components.dialogs.HomeDialog
import com.lalo.pixdev.ui.components.generalscomp.GeneralSettingsButton
import com.lalo.pixdev.ui.components.ProjectItem
import com.lalo.pixdev.ui.components.sprites.SpriteAnimation
import com.lalo.pixdev.ui.components.generalscomp.TypewriterText
import com.lalo.pixdev.ui.components.sprites.SpriteImage
import com.lalo.pixdev.ui.components.sprites.bouncyClickable
import com.lalo.pixdev.viewmodel.ProjectViewModel
import com.lalo.pixdev.viewmodel.model.Project
import kotlinx.coroutines.launch
import java.time.LocalTime
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ProjectViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var showAddProjectDialog by remember { mutableStateOf(false) }

    var showEditProjectDialog by remember { mutableStateOf(false) }
    var projectToEdit by remember { mutableStateOf<Project?>(null) }

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    var showHomeDialog by remember { mutableStateOf(false) }

    val projects by viewModel.projects.collectAsStateWithLifecycle(initialValue = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var expandedFilterMenu by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    val currentHour by remember {
        derivedStateOf { LocalTime.now().hour }
    }

    val greetingMessage by remember {
        derivedStateOf {
            when (currentHour) {
                in 6..11 -> context.getString(R.string.good_morning)
                in 12..18 -> context.getString(R.string.good_afternoon)
                else -> context.getString(R.string.good_night)
            }
        }
    }

    val normalPixIconResId = R.drawable.pixicon
    val pressedPixIconResId = R.drawable.pixicon_press

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 41.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GeneralSettingsButton(navController = navController)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
        ) {

            when (currentHour) {
                in 6..11 -> {
                    SpriteAnimation(
                        drawableResId = R.drawable.godmorning,
                        frameCount = 12,
                        startFrame = 0,
                        endFrame = 12,
                        animationDelayMillis = 120L,
                        animationScale = 3f,
                        loop = true,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onAnimationEnd = { }
                    )
                }
                in 12..18 -> {
                    SpriteAnimation(
                        drawableResId = R.drawable.godafternoon,
                        frameCount = 134,
                        startFrame = 0,
                        endFrame = 134,
                        animationDelayMillis = 120L,
                        animationScale = 3f,
                        loop = true,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onAnimationEnd = { }
                    )
                }
                else -> {
                    SpriteAnimation(
                        drawableResId = R.drawable.godnight,
                        frameCount = 39,
                        startFrame = 0,
                        endFrame = 39,
                        animationDelayMillis = 120L,
                        animationScale = 3f,
                        loop = true,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onAnimationEnd = { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))
            TypewriterText(
                text = greetingMessage,
                textStyle = MaterialTheme.typography.titleLarge.copy( fontSize = 40.sp, textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(),
                delayBetweenCharsMillis = 100L
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = context.getString(R.string.your_projects),
                    style = MaterialTheme.typography.titleLarge
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val homeButtonInteractionSource = remember { MutableInteractionSource() }
                    val homeButtonIsPressed by homeButtonInteractionSource.collectIsPressedAsState()
                    val currentHomeButtonIconResId = if (homeButtonIsPressed) pressedPixIconResId else normalPixIconResId

                    SpriteImage(
                        drawableResId = currentHomeButtonIconResId,
                        frameIndex = 4,
                        frameHeight = 64,
                        frameWidth = 64,
                        scale = 0.7f,
                        modifier = Modifier
                            .clip(RoundedCornerShape(0.dp))
                            .bouncyClickable(
                                onClick = { showHomeDialog = true },
                                pressedScale = 0.9f,
                                pressedDarkenFactor = 0f,
                                unpressedScale = 1f,
                                interactionSource = homeButtonInteractionSource
                            )
                    )
                    Spacer(Modifier.width(5.dp))

                    val addButtonInteractionSource = remember { MutableInteractionSource() }
                    val addButtonIsPressed by addButtonInteractionSource.collectIsPressedAsState()
                    val currentAddButtonIconResId = if (addButtonIsPressed) pressedPixIconResId else normalPixIconResId

                    SpriteImage(
                        drawableResId = currentAddButtonIconResId,
                        frameIndex = 1,
                        frameHeight = 64,
                        frameWidth = 64,
                        scale = 0.7f,
                        modifier = Modifier
                            .clip(RoundedCornerShape(0.dp))
                            .bouncyClickable(
                                onClick = {
                                    projectToEdit = null
                                    showAddProjectDialog = true
                                },
                                pressedScale = 0.9f,
                                pressedDarkenFactor = 0f,
                                unpressedScale = 1f,
                                interactionSource = addButtonInteractionSource
                            )
                    )

                    Spacer(Modifier.width(5.dp))

                    Box {
                        val filterButtonInteractionSource = remember { MutableInteractionSource() }
                        val filterButtonIsPressed by filterButtonInteractionSource.collectIsPressedAsState()
                        val currentFilterButtonIconResId = if (filterButtonIsPressed) pressedPixIconResId else normalPixIconResId

                        SpriteImage(
                            drawableResId = currentFilterButtonIconResId,
                            frameIndex = 2,
                            frameHeight = 64,
                            frameWidth = 64,
                            scale = 0.7f,
                            modifier = Modifier
                                .clip(RoundedCornerShape(0.dp))
                                .bouncyClickable(
                                    onClick = { expandedFilterMenu = true },
                                    pressedScale = 0.9f,
                                    pressedDarkenFactor = 0f,
                                    unpressedScale = 1f,
                                    interactionSource = filterButtonInteractionSource
                                )
                        )

                        DropdownMenu(
                            expanded = expandedFilterMenu,
                            onDismissRequest = { expandedFilterMenu = false }
                        ) {
                            ProjectViewModel.ProjectSortType.values().forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(text = context.getString(type.titleResId)) },
                                    onClick = {
                                        viewModel.sortProjectsBy(type)
                                        expandedFilterMenu = false
                                        coroutineScope.launch { listState.animateScrollToItem(0) }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (projects.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = context.getString(R.string.no_projects_message),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = projects,
                        key = { it.id }
                    ) { project ->
                        val completionPercentage by viewModel.getProjectCompletionPercentage(project.id)
                            .collectAsStateWithLifecycle(initialValue = 0)

                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                                    projectToDelete = project
                                    showDeleteConfirmationDialog = true
                                    false
                                } else {
                                    false
                                }
                            },
                            positionalThreshold = { totalDistance -> with(density) { 45.dp.toPx() } }
                        )

                        AnimatedVisibility(
                            visible = projects.contains(project),
                            enter = expandVertically(),
                            exit = shrinkVertically(animationSpec = tween(durationMillis = 300))
                        ) {
                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = true,
                                enableDismissFromEndToStart = true,
                                backgroundContent = {
                                    val target = dismissState.targetValue

                                    val alignment = when (target) {
                                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                        SwipeToDismissBoxValue.Settled -> Alignment.Center
                                    }
                                    val iconScaleAnimation by animateFloatAsState(
                                        if (target == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
                                        label = "iconScaleAnimation"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Red),
                                        contentAlignment = alignment
                                    ) {
                                        if (target != SwipeToDismissBoxValue.Settled) {
                                            SpriteImage(
                                                drawableResId = R.drawable.trash,
                                                frameIndex = 0,
                                                frameWidth = 32,
                                                frameHeight = 32,
                                                scale = 2f,
                                                modifier = Modifier.scale(iconScaleAnimation)
                                            )
                                        }
                                    }
                                },
                                content = {
                                    ProjectItem(
                                        project = project,
                                        onLongClick = {
                                            val success = viewModel.pinProject(project)
                                            if (!success) {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbarOnce(context.getString(R.string.pin_limit_message))
                                                }
                                            }
                                            coroutineScope.launch { listState.animateScrollToItem(0) }
                                        },
                                        onClick = {
                                            navController.navigate(Routes.requirements(project.id))
                                        },
                                        onDoubleClick = {
                                            projectToEdit = project
                                            showEditProjectDialog = true
                                        },
                                        completionPercentage = completionPercentage,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                },
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        if (showAddProjectDialog) {
            AddProjectDialog(
                onDismiss = { showAddProjectDialog = false },
                onConfirm = { projectName ->
                    val success = viewModel.addProject(projectName)
                    if (success) {
                        coroutineScope.launch { listState.animateScrollToItem(0) }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbarOnce(context.getString(R.string.project_limit_message))
                        }
                    }
                    showAddProjectDialog = false
                },
                initialProject = null,
                dialogTitle = context.getString(R.string.dialog_title_add_project)
            )
        }

        if (showEditProjectDialog && projectToEdit != null) {
            AddProjectDialog(
                onDismiss = {
                    showEditProjectDialog = false
                    projectToEdit = null
                },
                onConfirm = { updatedName ->
                    projectToEdit?.let { project ->
                        val updatedProject = project.copy(name = updatedName)
                        viewModel.updateProject(updatedProject)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbarOnce(context.getString(R.string.project_updated_message, updatedName))
                        }
                    }
                    showEditProjectDialog = false
                    projectToEdit = null
                },
                initialProject = projectToEdit,
                dialogTitle = context.getString(R.string.dialog_title_modify_project)
            )
        }

        if (showDeleteConfirmationDialog && projectToDelete != null) {
            DeleteProjectConfirmationDialog(
                projectName = projectToDelete!!.name,
                onConfirm = {
                    projectToDelete?.let { project ->
                        viewModel.deleteProject(project)
                    }
                    showDeleteConfirmationDialog = false
                    projectToDelete = null
                },
                onDismiss = {
                    showDeleteConfirmationDialog = false
                    projectToDelete = null
                }
            )
        }

        if (showHomeDialog) {
            HomeDialog(onDismissRequest = { showHomeDialog = false })
        }
    }
}

@Suppress("RedundantSuspendModifier")
suspend fun SnackbarHostState.showSnackbarOnce(message: String) {
    if (this.currentSnackbarData == null) {
        this.showSnackbar(
            message = message,
            withDismissAction = true
        )
    }
}