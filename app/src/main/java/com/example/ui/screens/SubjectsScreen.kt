package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ScheduleSlotEntity
import com.example.data.local.entity.StudentTaskEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.model.DayOfWeekHelper
import com.example.ui.components.AtmosphericBackground
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.TomorrowOverviewCard
import com.example.ui.viewmodel.PlannerUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    uiState: PlannerUiState,
    onAddNewSubject: () -> Unit,
    onEditSubject: (SubjectEntity) -> Unit,
    onDeleteSubject: (SubjectEntity) -> Unit,
    onDeleteSlot: (ScheduleSlotEntity) -> Unit,
    onToggleTaskCompleted: (StudentTaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Weekly Timetable, 1: Tomorrow's Schedule, 2: Subjects List
    var selectedDayInTimetable by remember {
        mutableIntStateOf(DayOfWeekHelper.getTodayAppDayNumber())
    }

    AtmosphericBackground(accentColor = MaterialTheme.colorScheme.secondary) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Screen Header Glass Card
                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(28.dp),
                    tintColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    glowColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "المواد والجدول الدراسي 🗓️",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "أضف موادك وجدولك الأسبوعي وتفقد جدول ومطلوبات الغد.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sub Tabs: Weekly Schedule vs Tomorrow vs Subjects List
                        SecondaryTabRow(
                            selectedTabIndex = selectedSubTab,
                            containerColor = Color.Transparent
                        ) {
                            Tab(
                                selected = selectedSubTab == 0,
                                onClick = { selectedSubTab = 0 },
                                text = { Text("الجدول الأسبوعي", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(17.dp)) }
                            )
                            Tab(
                                selected = selectedSubTab == 1,
                                onClick = { selectedSubTab = 1 },
                                text = { Text("جدول الغد 🔮", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                icon = { Icon(Icons.Default.Upcoming, contentDescription = null, modifier = Modifier.size(17.dp)) }
                            )
                            Tab(
                                selected = selectedSubTab == 2,
                                onClick = { selectedSubTab = 2 },
                                text = { Text("المواد (${uiState.subjects.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(17.dp)) }
                            )
                        }
                    }
                }

                when (selectedSubTab) {
                    0 -> {
                        // TAB 0: Weekly Timetable View
                        WeeklyTimetableContent(
                            uiState = uiState,
                            selectedDay = selectedDayInTimetable,
                            onSelectDay = { selectedDayInTimetable = it },
                            onDeleteSlot = onDeleteSlot
                        )
                    }
                    1 -> {
                        // TAB 1: DEDICATED TOMORROW SECTION IN SCHEDULE (Where user requested)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                TomorrowOverviewCard(
                                    slots = uiState.slots,
                                    tasks = uiState.tasks,
                                    onToggleTaskCompleted = onToggleTaskCompleted
                                )
                            }
                        }
                    }
                    else -> {
                        // TAB 2: Subjects List
                        SubjectsListContent(
                            uiState = uiState,
                            onEditSubject = onEditSubject,
                            onDeleteSubject = onDeleteSubject
                        )
                    }
                }
            }

            // Floating Action Button to Add Subject
            ExtendedFloatingActionButton(
                onClick = onAddNewSubject,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 85.dp, end = 20.dp)
                    .testTag("fab_add_subject"),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة مادة")
                Spacer(modifier = Modifier.width(8.dp))
                Text("إضافة مادة جديدة", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun WeeklyTimetableContent(
    uiState: PlannerUiState,
    selectedDay: Int,
    onSelectDay: (Int) -> Unit,
    onDeleteSlot: (ScheduleSlotEntity) -> Unit
) {
    val daySlots = uiState.slots
        .filter { it.slot.dayOfWeek == selectedDay }
        .sortedWith(compareBy({ it.slot.hour }, { it.slot.minute }))

    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Day Selector
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DayOfWeekHelper.daysList) { (dayNum, dayName) ->
                val isSelected = selectedDay == dayNum
                val isToday = DayOfWeekHelper.getTodayAppDayNumber() == dayNum
                val isTomorrow = DayOfWeekHelper.getTomorrowAppDayNumber() == dayNum
                val countForDay = uiState.slots.count { it.slot.dayOfWeek == dayNum }

                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectDay(dayNum) },
                    label = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = when {
                                    isToday -> "$dayName (اليوم)"
                                    isTomorrow -> "$dayName (غداً)"
                                    else -> dayName
                                },
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                            if (countForDay > 0) {
                                Text(
                                    text = "$countForDay حصص",
                                    fontSize = 10.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (daySlots.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد حصص مجدولة ليوم ${DayOfWeekHelper.getDayName(selectedDay)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "أضف مادة وحدد أيام حصصها لتظهر في هذا اليوم.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(daySlots, key = { it.slot.id }) { slotWithSubject ->
                    val subject = slotWithSubject.subject
                    val slot = slotWithSubject.slot
                    val color = try {
                        Color(android.graphics.Color.parseColor(subject?.colorHex ?: "#2563EB"))
                    } catch (_: Exception) {
                        MaterialTheme.colorScheme.primary
                    }

                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        tintColor = color.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = subject?.name ?: "مادة",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (!subject?.teacherName.isNullOrBlank()) {
                                        Text(
                                            text = "المعلم: ${subject?.teacherName}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Time Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = color.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = color,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = DayOfWeekHelper.formatTime(slot.hour, slot.minute),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = color
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDeleteSlot(slot) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف موعد الحصة",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectsListContent(
    uiState: PlannerUiState,
    onEditSubject: (SubjectEntity) -> Unit,
    onDeleteSubject: (SubjectEntity) -> Unit
) {
    if (uiState.subjects.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "لا توجد مواد مضافة بعد!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "أضف موادك الدراسية مثل الرياضيات، اللغة العربية، العلوم مع مواعيد حصصها.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.subjects, key = { it.id }) { subject ->
                val subjectSlots = uiState.slots
                    .filter { it.slot.subjectId == subject.id }
                    .sortedWith(compareBy({ it.slot.dayOfWeek }, { it.slot.hour }))

                val subjectTasksCount = uiState.tasks.count { it.task.subjectId == subject.id && !it.task.isCompleted }

                val color = try {
                    Color(android.graphics.Color.parseColor(subject.colorHex))
                } catch (_: Exception) {
                    MaterialTheme.colorScheme.primary
                }

                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    tintColor = color.copy(alpha = 0.12f)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = subject.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (subject.teacherName.isNotBlank()) {
                                        Text(
                                            text = "المعلم: ${subject.teacherName}",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onEditSubject(subject) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "تعديل المادة",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteSubject(subject) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف المادة",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Scheduled Class Times across week
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "أيام ومواعيد الحصص (${subjectSlots.size} أيام):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (subjectSlots.isEmpty()) {
                            Text(
                                text = "لم تحدد مواعيد حصص لهذه المادة بعد.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                subjectSlots.forEach { slotItem ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = color.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = "${DayOfWeekHelper.getDayName(slotItem.slot.dayOfWeek)} ${DayOfWeekHelper.formatTime(slotItem.slot.hour, slotItem.slot.minute)}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = color,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Pending tasks count
                        if (subjectTasksCount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = "لديك $subjectTasksCount مطلوبات ومذاكرة قيد الانتظار لهذه المادة",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
