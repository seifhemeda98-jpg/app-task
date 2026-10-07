package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.StudentTaskEntity
import com.example.data.local.entity.SubjectEntity
import com.example.ui.dialogs.AddSubjectDialog
import com.example.ui.dialogs.AddTaskBottomSheet
import com.example.ui.dialogs.IosGlassThemeModal
import com.example.ui.theme.getLiquidGlassBorder
import com.example.ui.viewmodel.PlannerUiState
import com.example.ui.viewmodel.StudentPlannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    viewModel: StudentPlannerViewModel,
    uiState: PlannerUiState
) {
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddTaskSheet by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<StudentTaskEntity?>(null) }

    var showAddSubjectSheet by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<SubjectEntity?>(null) }

    // State for iOS 26 Liquid Glass Theme Modal
    var showThemeModal by remember { mutableStateOf(false) }

    // Request notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Feedback message display
    LaunchedEffect(uiState.userFeedbackMessage) {
        uiState.userFeedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    // Wrap with RTL layout direction for Arabic
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "مهام الطالب 🎓",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    actions = {
                        // iOS-Style Glass Palette button on TopAppBar
                        IconButton(
                            onClick = { showThemeModal = true },
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .testTag("topbar_btn_palette")
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.22f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.45f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "تخصيص ألوان Liquid Glass",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(7.dp)
                                        .size(19.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                // Floating iOS-style Liquid Glass Navigation Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(32.dp),
                                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                            )
                            .border(
                                width = 1.2.dp,
                                brush = getLiquidGlassBorder(),
                                shape = RoundedCornerShape(32.dp)
                            ),
                        shape = RoundedCornerShape(32.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            tonalElevation = 0.dp
                        ) {
                            val pendingTasksCount = uiState.tasks.count { !it.task.isCompleted }

                            // Tab 0: Tasks / Home
                            NavigationBarItem(
                                selected = uiState.selectedTab == 0,
                                onClick = { viewModel.setSelectedTab(0) },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (pendingTasksCount > 0) {
                                                Badge { Text("$pendingTasksCount") }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.selectedTab == 0) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                                            contentDescription = "المهام والمطلوبات"
                                        )
                                    }
                                },
                                label = { Text("المطلوبات", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.testTag("nav_tab_tasks")
                            )

                            // Tab 1: Subjects & Schedule
                            NavigationBarItem(
                                selected = uiState.selectedTab == 1,
                                onClick = { viewModel.setSelectedTab(1) },
                                icon = {
                                    Icon(
                                        imageVector = if (uiState.selectedTab == 1) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                        contentDescription = "المواد والجدول"
                                    )
                                },
                                label = { Text("الجدول والمواد", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.testTag("nav_tab_subjects")
                            )

                            // Tab 2: Reminders & Alerts
                            NavigationBarItem(
                                selected = uiState.selectedTab == 2,
                                onClick = { viewModel.setSelectedTab(2) },
                                icon = {
                                    Icon(
                                        imageVector = if (uiState.selectedTab == 2) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                        contentDescription = "التذكيرات والتنبيهات"
                                    )
                                },
                                label = { Text("التذكيرات", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.testTag("nav_tab_reminders")
                            )
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AnimatedContent(
                    targetState = uiState.selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tabContent"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> HomeScreen(
                            uiState = uiState,
                            onFilterChanged = { viewModel.setTaskFilter(it) },
                            onSubjectFilterChanged = { viewModel.setSubjectFilter(it) },
                            onSearchChanged = { viewModel.setSearchQuery(it) },
                            onToggleTaskCompleted = { viewModel.toggleTaskCompletion(it) },
                            onEditTask = {
                                taskToEdit = it
                                showAddTaskSheet = true
                            },
                            onDeleteTask = { viewModel.deleteTask(it) },
                            onAddNewTask = {
                                taskToEdit = null
                                showAddTaskSheet = true
                            },
                            onOpenThemeModal = { showThemeModal = true }
                        )

                        1 -> SubjectsScreen(
                            uiState = uiState,
                            onAddNewSubject = {
                                subjectToEdit = null
                                showAddSubjectSheet = true
                            },
                            onEditSubject = {
                                subjectToEdit = it
                                showAddSubjectSheet = true
                            },
                            onDeleteSubject = { viewModel.deleteSubject(it) },
                            onDeleteSlot = { viewModel.deleteSlot(it) },
                            onToggleTaskCompleted = { viewModel.toggleTaskCompletion(it) }
                        )

                        2 -> RemindersScreen(
                            uiState = uiState,
                            onNightReminderToggle = { viewModel.toggleNightReminder(it) },
                            onOneHourToggle = { viewModel.toggleOneHourBefore(it) },
                            onTomorrowSummaryToggle = { viewModel.toggleTomorrowSummary(it) },
                            onNightTimeSelected = { h, m -> viewModel.updateNightReminderTime(h, m) },
                            onTriggerTestNotification = { viewModel.triggerInstantTestNotification(it) }
                        )
                    }
                }
            }
        }

        // Add or Edit Task Bottom Sheet
        if (showAddTaskSheet) {
            AddTaskBottomSheet(
                subjects = uiState.subjects,
                taskToEdit = taskToEdit,
                onDismiss = {
                    showAddTaskSheet = false
                    taskToEdit = null
                },
                onSaveTask = { id, subId, due, dayOfWeek, mem, hw, notes, rNight, rDay, rHour ->
                    viewModel.saveTask(
                        id = id,
                        subjectId = subId,
                        dueDateMillis = due,
                        targetDayOfWeek = dayOfWeek,
                        memorization = mem,
                        homework = hw,
                        notes = notes,
                        remindNightBefore = rNight,
                        remindDayBefore = rDay,
                        remindOneHourBefore = rHour
                    )
                },
                onOpenAddSubject = {
                    showAddTaskSheet = false
                    subjectToEdit = null
                    showAddSubjectSheet = true
                },
                getNextClassForSubject = { subId ->
                    viewModel.getNextUpcomingClassForSubject(subId)
                }
            )
        }

        // Add or Edit Subject Bottom Sheet
        if (showAddSubjectSheet) {
            val existingSlotsForEdit = if (subjectToEdit != null) {
                uiState.slots
                    .filter { it.slot.subjectId == subjectToEdit!!.id }
                    .map { Pair(it.slot.dayOfWeek, Pair(it.slot.hour, it.slot.minute)) }
            } else {
                emptyList()
            }

            AddSubjectDialog(
                subjectToEdit = subjectToEdit,
                existingSlots = existingSlotsForEdit,
                onDismiss = {
                    showAddSubjectSheet = false
                    subjectToEdit = null
                },
                onSaveSubject = { id, name, teacher, colorHex, notes, slots ->
                    viewModel.saveSubject(
                        id = id,
                        name = name,
                        teacherName = teacher,
                        colorHex = colorHex,
                        notes = notes,
                        scheduleSlots = slots
                    )
                }
            )
        }

        // iOS 26 Liquid Glass Theme Modal
        if (showThemeModal) {
            IosGlassThemeModal(
                currentAccent = uiState.selectedThemeAccent,
                onSelectAccent = { accent ->
                    viewModel.setThemeAccent(accent)
                },
                onDismiss = { showThemeModal = false }
            )
        }
    }
}
