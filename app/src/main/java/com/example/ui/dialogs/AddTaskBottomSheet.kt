package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.local.entity.StudentTaskEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.model.DayOfWeekHelper
import com.example.ui.components.LiquidGlassCard
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTaskBottomSheet(
    subjects: List<SubjectEntity>,
    taskToEdit: StudentTaskEntity?,
    onDismiss: () -> Unit,
    onSaveTask: (
        id: Long,
        subjectId: Long,
        dueDateMillis: Long,
        targetDayOfWeek: Int,
        memorization: String,
        homework: String,
        notes: String,
        remindNight: Boolean,
        remindDayBefore: Boolean,
        remindOneHour: Boolean
    ) -> Unit,
    onOpenAddSubject: () -> Unit,
    getNextClassForSubject: (Long) -> Pair<Int, Long>?
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedSubjectId by remember {
        mutableLongStateOf(taskToEdit?.subjectId ?: (subjects.firstOrNull()?.id ?: 0L))
    }

    var memorizationText by remember { mutableStateOf(taskToEdit?.memorization ?: "") }
    var homeworkText by remember { mutableStateOf(taskToEdit?.homework ?: "") }
    var notesText by remember { mutableStateOf(taskToEdit?.notes ?: "") }

    var targetDayNumber by remember {
        mutableIntStateOf(taskToEdit?.targetDayOfWeek ?: DayOfWeekHelper.getTodayAppDayNumber())
    }

    var dueDateMillis by remember {
        mutableLongStateOf(taskToEdit?.dueDateMillis ?: System.currentTimeMillis())
    }

    var remindNight by remember { mutableStateOf(taskToEdit?.remindNightBefore ?: true) }
    var remindDayBefore by remember { mutableStateOf(taskToEdit?.remindDayBefore ?: true) }
    var remindOneHour by remember { mutableStateOf(taskToEdit?.remindOneHourBefore ?: true) }

    var validationError by remember { mutableStateOf<String?>(null) }

    // If initial selected subject has an upcoming class, auto set date if creating new task
    LaunchedEffect(selectedSubjectId) {
        if (taskToEdit == null && selectedSubjectId != 0L) {
            val nextClass = getNextClassForSubject(selectedSubjectId)
            if (nextClass != null) {
                targetDayNumber = nextClass.first
                dueDateMillis = nextClass.second
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (taskToEdit == null) "إضافة مطلوب أو مهمة جديدة 📝" else "تعديل المطلوب ✏️",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Choose Subject
            Text(
                text = "1. حدد المادة الدراسية:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (subjects.isEmpty()) {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    tintColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لم تقم بإضافة مواد دراسية بعد!",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onOpenAddSubject,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("أضف أول مادة دراسية الآن")
                        }
                    }
                }
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    subjects.forEach { subject ->
                        val isSelected = subject.id == selectedSubjectId
                        val color = try {
                            Color(android.graphics.Color.parseColor(subject.colorHex))
                        } catch (_: Exception) {
                            MaterialTheme.colorScheme.primary
                        }

                        Surface(
                            onClick = { selectedSubjectId = subject.id },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.testTag("subject_chip_${subject.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = subject.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Button to add another subject quickly
                    Surface(
                        onClick = onOpenAddSubject,
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "مادة جديدة",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Choose Day / Date
            Text(
                text = "2. موعد ويوم الحصة:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick helpers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Today
                FilterChip(
                    selected = targetDayNumber == DayOfWeekHelper.getTodayAppDayNumber(),
                    onClick = {
                        targetDayNumber = DayOfWeekHelper.getTodayAppDayNumber()
                        dueDateMillis = System.currentTimeMillis()
                    },
                    shape = RoundedCornerShape(14.dp),
                    label = { Text("اليوم (${DayOfWeekHelper.getDayName(DayOfWeekHelper.getTodayAppDayNumber())})") }
                )

                // Tomorrow
                FilterChip(
                    selected = targetDayNumber == DayOfWeekHelper.getTomorrowAppDayNumber(),
                    onClick = {
                        targetDayNumber = DayOfWeekHelper.getTomorrowAppDayNumber()
                        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
                        dueDateMillis = cal.timeInMillis
                    },
                    shape = RoundedCornerShape(14.dp),
                    label = { Text("غداً (${DayOfWeekHelper.getDayName(DayOfWeekHelper.getTomorrowAppDayNumber())})") }
                )
            }

            // Days of week selector
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                DayOfWeekHelper.daysList.forEach { (dayNum, dayName) ->
                    val isDaySelected = targetDayNumber == dayNum
                    FilterChip(
                        selected = isDaySelected,
                        onClick = {
                            targetDayNumber = dayNum
                            dueDateMillis = DayOfWeekHelper.getUpcomingMillisForDayNumber(dayNum)
                        },
                        shape = RoundedCornerShape(14.dp),
                        label = { Text(dayName, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Memorization / Recitation
            Text(
                text = "3. خانة التسميع أو الحفظ: 📖",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF16A34A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = memorizationText,
                onValueChange = { memorizationText = it },
                label = { Text("المطلوب حفظه أو تسميعه") },
                placeholder = { Text("مثال: حفظ سورة الملك من آية 1 إلى 15، أو مفردات الوحدة الأولى") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_task_memorization"),
                shape = RoundedCornerShape(18.dp),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Homework
            Text(
                text = "4. خانة الواجب المنزلي: ✍️",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = homeworkText,
                onValueChange = { homeworkText = it },
                label = { Text("الواجب المطلوب حله أو كتابته") },
                placeholder = { Text("مثال: حل تمارين ص 42 مسائل 1 و 3 و 5، أو حل ورقة العمل رقم 2") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_task_homework"),
                shape = RoundedCornerShape(18.dp),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Additional Notes
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text("ملاحظات إضافية (اختياري)") },
                placeholder = { Text("مثال: إحضار الأدوات الهندسية أو الألوان...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_task_notes"),
                shape = RoundedCornerShape(18.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 6. Smart Reminders Switches
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                tintColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔔 تفعيل التذكيرات الذكية لهذا المطلوب:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Night reminder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NightlightRound,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("تذكير ليلي بالمذاكرة", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text("تنبيه مساءً لمراجعة الحفظ وإنهاء الواجب", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = remindNight,
                            onCheckedChange = { remindNight = it },
                            modifier = Modifier.testTag("switch_remind_night")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 1 hour before reminder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("تذكير قبل الحصة بساعة", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text("تنبيه قبل موعد الحصة لتجهيز الكشكول والمراجعة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = remindOneHour,
                            onCheckedChange = { remindOneHour = it },
                            modifier = Modifier.testTag("switch_remind_hour")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Day before reminder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("تذكير قبل اليوم بـ 24 ساعة", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text("إشعار مسبق لتجنب تراكم الواجبات", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = remindDayBefore,
                            onCheckedChange = { remindDayBefore = it },
                            modifier = Modifier.testTag("switch_remind_day")
                        )
                    }
                }
            }

            if (validationError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = validationError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            Button(
                onClick = {
                    if (selectedSubjectId == 0L) {
                        validationError = "برجاء اختيار المادة الدراسية أولاً"
                        return@Button
                    }
                    if (memorizationText.isBlank() && homeworkText.isBlank()) {
                        validationError = "برجاء إدخال التسميع/الحفظ أو الواجب المطلوب"
                        return@Button
                    }

                    onSaveTask(
                        taskToEdit?.id ?: 0L,
                        selectedSubjectId,
                        dueDateMillis,
                        targetDayNumber,
                        memorizationText,
                        homeworkText,
                        notesText,
                        remindNight,
                        remindDayBefore,
                        remindOneHour
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_save_task"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (taskToEdit == null) "حفظ المطلوب وتفعيل التذكيرات" else "حفظ التعديلات",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
