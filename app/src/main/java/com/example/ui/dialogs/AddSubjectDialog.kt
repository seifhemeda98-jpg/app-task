package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.local.entity.SubjectEntity
import com.example.data.model.DayOfWeekHelper
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.SubjectColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddSubjectDialog(
    subjectToEdit: SubjectEntity?,
    existingSlots: List<Pair<Int, Pair<Int, Int>>> = emptyList(),
    onDismiss: () -> Unit,
    onSaveSubject: (
        id: Long,
        name: String,
        teacher: String,
        colorHex: String,
        notes: String,
        slots: List<Pair<Int, Pair<Int, Int>>>
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var subjectName by remember { mutableStateOf(subjectToEdit?.name ?: "") }
    var teacherName by remember { mutableStateOf(subjectToEdit?.teacherName ?: "") }
    var selectedColorHex by remember { mutableStateOf(subjectToEdit?.colorHex ?: "#2563EB") }
    var notesText by remember { mutableStateOf(subjectToEdit?.notes ?: "") }

    // List of schedule slots: Pair<dayOfWeek, Pair<hour, minute>>
    val slotsList = remember {
        mutableStateListOf<Pair<Int, Pair<Int, Int>>>().apply {
            addAll(existingSlots)
        }
    }

    // Multi-day selection state! Allows selecting multiple days at once
    val selectedDaysForNewSlot = remember {
        mutableStateListOf<Int>().apply {
            // Default select Sunday & Tuesday
            add(2)
            add(4)
        }
    }

    var selectedHour by remember { mutableIntStateOf(9) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var showTimePickerSheet by remember { mutableStateOf(false) }

    var validationError by remember { mutableStateOf<String?>(null) }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (subjectToEdit == null) "إضافة مادة لأيام متعددة 📚" else "تعديل المادة وجدولها ✏️",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subject Name
            OutlinedTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                label = { Text("اسم المادة *") },
                placeholder = { Text("مثال: الرياضيات، الفيزياء، لغة عربية، أحياء...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_subject_name"),
                shape = RoundedCornerShape(18.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Teacher Name
            OutlinedTextField(
                value = teacherName,
                onValueChange = { teacherName = it },
                label = { Text("اسم المعلم أو المحاضر (اختياري)") },
                placeholder = { Text("مثال: أ/ محمد عبد الله") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_teacher_name"),
                shape = RoundedCornerShape(18.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Color Picker
            Text(
                text = "لون تمييز المادة:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubjectColors.forEach { color ->
                    val hexString = String.format("#%06X", (0xFFFFFF and color.value.toInt()))
                    val isColorSelected = selectedColorHex.equals(hexString, ignoreCase = true)

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isColorSelected) 3.dp else 1.dp,
                                color = if (isColorSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedColorHex = hexString },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isColorSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Schedule Slots Setup (MULTI-DAY SELECTION SUPPORT)
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                tintColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "📅 تحديد أيام الحصص وساعتها (أكثر من يوم):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "يمكنك اختيار عدة أيام معاً (مثال: الأحد والثلاثاء والخميس) وإضافتها بنقرة واحدة.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Multi-Day Presets
                    Text(
                        text = "اختيار سريع للأيام:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Preset 1: Sun + Tue + Thu
                        Surface(
                            onClick = {
                                selectedDaysForNewSlot.clear()
                                selectedDaysForNewSlot.addAll(listOf(2, 4, 6)) // Sun, Tue, Thu
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "أحد + ثلاثاء + خميس",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }

                        // Preset 2: Sat + Mon + Wed
                        Surface(
                            onClick = {
                                selectedDaysForNewSlot.clear()
                                selectedDaysForNewSlot.addAll(listOf(1, 3, 5)) // Sat, Mon, Wed
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "سبت + إثنين + أربعاء",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }

                        // Preset 3: All Weekdays
                        Surface(
                            onClick = {
                                selectedDaysForNewSlot.clear()
                                selectedDaysForNewSlot.addAll(listOf(1, 2, 3, 4, 5, 6))
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "أسبوعياً",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Day selector chips (Multi-select)
                    Text(
                        text = "حدد أيام الأسبوع لهذه المادة:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DayOfWeekHelper.daysList.forEach { (dNum, dName) ->
                            val isDaySelected = selectedDaysForNewSlot.contains(dNum)
                            FilterChip(
                                selected = isDaySelected,
                                onClick = {
                                    if (isDaySelected) {
                                        selectedDaysForNewSlot.remove(dNum)
                                    } else {
                                        selectedDaysForNewSlot.add(dNum)
                                    }
                                },
                                label = { Text(dName, fontSize = 12.sp, fontWeight = if (isDaySelected) FontWeight.Bold else FontWeight.Normal) },
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = if (isDaySelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Time selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "موعد الحصة: ${DayOfWeekHelper.formatTime(selectedHour, selectedMinute)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { showTimePickerSheet = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("تغيير الساعة", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Add to Multiple Days Button
                    Button(
                        onClick = {
                            if (selectedDaysForNewSlot.isEmpty()) {
                                validationError = "برجاء تحديد يوم واحد على الأقل"
                                return@Button
                            }
                            selectedDaysForNewSlot.forEach { dayNum ->
                                val slot = Pair(dayNum, Pair(selectedHour, selectedMinute))
                                if (!slotsList.contains(slot)) {
                                    slotsList.add(slot)
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_add_slot_to_subject"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "إضافة الحصة للأيام المحددة (${selectedDaysForNewSlot.size} أيام)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Display all added slots
                    if (slotsList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "مواعيد الحصص المضافة في الجدول (${slotsList.size}):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            slotsList.sortedBy { it.first }.forEach { slot ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "${DayOfWeekHelper.getDayName(slot.first)} ${DayOfWeekHelper.formatTime(slot.second.first, slot.second.second)}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "حذف الموعد",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier
                                                .size(15.dp)
                                                .clickable { slotsList.remove(slot) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Notes
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text("ملاحظات عن المادة (اختياري)") },
                placeholder = { Text("مثال: يحتاج كشكول 100 ورقة، مراجعة أسبوعية...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_subject_notes"),
                shape = RoundedCornerShape(18.dp)
            )

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

            // Save Subject Button
            Button(
                onClick = {
                    if (subjectName.isBlank()) {
                        validationError = "برجاء إدخال اسم المادة"
                        return@Button
                    }
                    onSaveSubject(
                        subjectToEdit?.id ?: 0L,
                        subjectName,
                        teacherName,
                        selectedColorHex,
                        notesText,
                        slotsList.toList()
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_save_subject"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (subjectToEdit == null) "حفظ المادة وجدولها" else "حفظ التعديلات",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Time Picker Bottom Sheet
    if (showTimePickerSheet) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            is24Hour = false
        )
        ModalBottomSheet(
            onDismissRequest = { showTimePickerSheet = false },
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "حدد موعد الحصة:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TimePicker(state = timePickerState)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        selectedHour = timePickerState.hour
                        selectedMinute = timePickerState.minute
                        showTimePickerSheet = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("تأكيد الوقت")
                }
            }
        }
    }
}
