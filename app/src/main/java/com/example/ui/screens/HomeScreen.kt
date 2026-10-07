package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.StudentTaskEntity
import com.example.data.model.DayOfWeekHelper
import com.example.ui.components.AtmosphericBackground
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.TaskCard
import com.example.ui.viewmodel.PlannerUiState
import com.example.ui.viewmodel.TaskFilter

@Composable
fun HomeScreen(
    uiState: PlannerUiState,
    onFilterChanged: (TaskFilter) -> Unit,
    onSubjectFilterChanged: (Long?) -> Unit,
    onSearchChanged: (String) -> Unit,
    onToggleTaskCompleted: (StudentTaskEntity) -> Unit,
    onEditTask: (StudentTaskEntity) -> Unit,
    onDeleteTask: (StudentTaskEntity) -> Unit,
    onAddNewTask: () -> Unit,
    onOpenThemeModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayName = DayOfWeekHelper.getDayName(DayOfWeekHelper.getTodayAppDayNumber())
    val totalTasks = uiState.tasks.size
    val completedTasks = uiState.tasks.count { it.task.isCompleted }
    val pendingTasks = totalTasks - completedTasks

    AtmosphericBackground(accentColor = MaterialTheme.colorScheme.primary) {
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Glass Card
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        tintColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "مرحباً يا بطل 🎓",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "مطلوبات ومهام الدراسة",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // iOS-Style Glass Theme Customizer Button (Prominent & Clickable)
                                    Surface(
                                        onClick = onOpenThemeModal,
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
                                        border = androidx.compose.foundation.BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .testTag("btn_open_theme_modal")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Palette,
                                                contentDescription = "تخصيص الألوان زجاجي",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(17.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = "الألوان 🎨",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    // Day badge
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = todayName,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Stats Summary with iOS Glass Cards
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatBox(
                                    title = "الكل",
                                    count = totalTasks,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                StatBox(
                                    title = "المتبقي",
                                    count = pendingTasks,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.weight(1f)
                                )
                                StatBox(
                                    title = "أُنجزت",
                                    count = completedTasks,
                                    color = Color(0xFF16A34A),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Search Bar in Glass container
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = 2.dp
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchChanged,
                            placeholder = { Text("ابحث في التسميع، الواجب، أو المادة...") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "بحث", tint = MaterialTheme.colorScheme.primary)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_tasks_input"),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true
                        )
                    }
                }

                // Status Filter Chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(TaskFilter.values()) { filter ->
                            FilterChip(
                                selected = uiState.taskFilter == filter,
                                onClick = { onFilterChanged(filter) },
                                label = { Text(filter.title, fontWeight = FontWeight.Medium) },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("filter_chip_${filter.name}")
                            )
                        }
                    }
                }

                // Subject Filter Chips (If subjects exist)
                if (uiState.subjects.isNotEmpty()) {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                FilterChip(
                                    selected = uiState.selectedSubjectFilterId == null,
                                    onClick = { onSubjectFilterChanged(null) },
                                    label = { Text("جميع المواد") },
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }
                            items(uiState.subjects) { subject ->
                                val isSelected = uiState.selectedSubjectFilterId == subject.id
                                val color = try {
                                    Color(android.graphics.Color.parseColor(subject.colorHex))
                                } catch (_: Exception) {
                                    MaterialTheme.colorScheme.primary
                                }
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSubjectFilterChanged(if (isSelected) null else subject.id) },
                                    label = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(subject.name)
                                        }
                                    },
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }
                        }
                    }
                }

                // Task List or Empty State
                if (uiState.filteredTasks.isEmpty()) {
                    item {
                        LiquidGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(26.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AssignmentTurnedIn,
                                    contentDescription = null,
                                    modifier = Modifier.size(54.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (uiState.tasks.isEmpty()) "لا توجد أي مطلوبات حالياً!" else "لا توجد نتائج تطابق التصفية",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "اضغط على زر (إضافة مطلوب) بالأسفل لتسجيل التسميع والواجب وتفعيل التنبيهات.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = uiState.filteredTasks,
                        key = { it.task.id }
                    ) { taskWithSubject ->
                        TaskCard(
                            taskWithSubject = taskWithSubject,
                            onToggleCompleted = { onToggleTaskCompleted(taskWithSubject.task) },
                            onEdit = { onEditTask(taskWithSubject.task) },
                            onDelete = { onDeleteTask(taskWithSubject.task) }
                        )
                    }
                }
            }

            // Floating Action Button to Add Task with iOS 26 rounded squircle
            ExtendedFloatingActionButton(
                onClick = onAddNewTask,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 85.dp, end = 20.dp)
                    .testTag("fab_add_task"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة مطلوب")
                Spacer(modifier = Modifier.width(8.dp))
                Text("إضافة مطلوب أو مهمة", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatBox(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = color.copy(alpha = 0.14f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
