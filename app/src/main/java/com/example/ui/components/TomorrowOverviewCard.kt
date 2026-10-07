package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.StudentTaskEntity
import com.example.data.model.DayOfWeekHelper
import com.example.data.model.SlotWithSubject
import com.example.data.model.TaskWithSubject
import java.util.Calendar

@Composable
fun TomorrowOverviewCard(
    slots: List<SlotWithSubject>,
    tasks: List<TaskWithSubject>,
    onToggleTaskCompleted: (StudentTaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val tomorrowDayNumber = DayOfWeekHelper.getTomorrowAppDayNumber()
    val tomorrowName = DayOfWeekHelper.getDayName(tomorrowDayNumber)

    // Tomorrow's slots
    val tomorrowSlots = slots
        .filter { it.slot.dayOfWeek == tomorrowDayNumber }
        .sortedWith(compareBy({ it.slot.hour }, { it.slot.minute }))

    // Tomorrow's tasks
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val tomorrowStart = todayStart + (24 * 60 * 60 * 1000)
    val dayAfterTomorrowStart = tomorrowStart + (24 * 60 * 60 * 1000)

    val tomorrowTasks = tasks.filter {
        it.task.dueDateMillis in tomorrowStart until dayAfterTomorrowStart ||
                (it.task.targetDayOfWeek == tomorrowDayNumber && !it.task.isCompleted)
    }

    var isExpanded by remember { mutableStateOf(true) }

    LiquidGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tomorrow_overview_card"),
        shape = RoundedCornerShape(28.dp),
        tintColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upcoming,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ماذا لديّ غداً؟ 🔮",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = tomorrowName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${tomorrowSlots.size} حصص • ${tomorrowTasks.count { !it.task.isCompleted }} مطلوبات معلقة",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    // Subsection A: Tomorrow's Scheduled Classes
                    Text(
                        text = "📚 حصص الغد في الجدول (${tomorrowSlots.size}):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (tomorrowSlots.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "لا توجد حصص مجدولة ليوم $tomorrowName، يوم راحة ومراجعة! ✨",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            tomorrowSlots.forEach { slotItem ->
                                val sub = slotItem.subject
                                val slot = slotItem.slot
                                val subColor = try {
                                    Color(android.graphics.Color.parseColor(sub?.colorHex ?: "#2563EB"))
                                } catch (_: Exception) {
                                    MaterialTheme.colorScheme.primary
                                }

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, subColor.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(subColor)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = sub?.name ?: "مادة",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            if (!sub?.teacherName.isNullOrBlank()) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "(${sub?.teacherName})",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = subColor,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = DayOfWeekHelper.formatTime(slot.hour, slot.minute),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = subColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Subsection B: Tomorrow's Tasks & Homework
                    Text(
                        text = "✍️ مطلوبات وواجبات الغد (${tomorrowTasks.size}):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (tomorrowTasks.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ليس عليك واجبات أو تسميع مسجل لغد! 🎉 أحسنت يا بطل.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            tomorrowTasks.forEach { taskItem ->
                                val task = taskItem.task
                                val sub = taskItem.subject

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (task.isCompleted) Color(0xFF16A34A).copy(alpha = 0.08f)
                                    else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (task.isCompleted) Color(0xFF16A34A).copy(alpha = 0.3f)
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = sub?.name ?: "مادة",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            if (task.memorization.isNotBlank()) {
                                                Text(
                                                    text = "📖 حفظ: ${task.memorization}",
                                                    fontSize = 12.sp,
                                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                                )
                                            }
                                            if (task.homework.isNotBlank()) {
                                                Text(
                                                    text = "✍️ واجب: ${task.homework}",
                                                    fontSize = 12.sp,
                                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                                )
                                            }
                                        }

                                        Icon(
                                            imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                            contentDescription = "إنجاز",
                                            tint = if (task.isCompleted) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clickable { onToggleTaskCompleted(task) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
