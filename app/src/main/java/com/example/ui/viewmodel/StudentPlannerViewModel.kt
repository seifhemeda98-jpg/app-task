package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.StudentPlannerApp
import com.example.data.local.entity.ScheduleSlotEntity
import com.example.data.local.entity.StudentTaskEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.model.DayOfWeekHelper
import com.example.data.model.SlotWithSubject
import com.example.data.model.TaskWithSubject
import com.example.data.repository.StudentRepository
import com.example.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

enum class TaskFilter(val title: String) {
    ALL("الكل"),
    TODAY("اليوم"),
    TOMORROW("الغد"),
    PENDING("قيد الإنجاز"),
    COMPLETED("المكتملة")
}

data class PlannerUiState(
    val tasks: List<TaskWithSubject> = emptyList(),
    val filteredTasks: List<TaskWithSubject> = emptyList(),
    val subjects: List<SubjectEntity> = emptyList(),
    val slots: List<SlotWithSubject> = emptyList(),
    val selectedTab: Int = 0, // 0: Tasks, 1: Subjects/Schedule, 2: Reminders
    val taskFilter: TaskFilter = TaskFilter.ALL,
    val selectedSubjectFilterId: Long? = null,
    val searchQuery: String = "",
    val nightReminderHour: Int = 20, // 8:30 PM default
    val nightReminderMinute: Int = 30,
    val isTomorrowSummaryEnabled: Boolean = true,
    val isOneHourBeforeEnabled: Boolean = true,
    val isNightReminderEnabled: Boolean = true,
    val selectedThemeAccent: com.example.ui.theme.ThemeAccent = com.example.ui.theme.ThemeAccent.SAPPHIRE,
    val userFeedbackMessage: String? = null
)

class StudentPlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudentRepository

    init {
        val app = application as StudentPlannerApp
        val db = app.database
        repository = StudentRepository(
            subjectDao = db.subjectDao(),
            scheduleSlotDao = db.scheduleSlotDao(),
            studentTaskDao = db.studentTaskDao()
        )
    }

    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = combine(
        _uiState,
        repository.allTasksWithSubjects,
        repository.allSubjects,
        repository.allSlotsWithSubjects
    ) { current, tasks, subjects, slots ->
        val filtered = filterTasks(tasks, current.taskFilter, current.selectedSubjectFilterId, current.searchQuery)
        current.copy(
            tasks = tasks,
            filteredTasks = filtered,
            subjects = subjects,
            slots = slots
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlannerUiState()
    )

    private fun filterTasks(
        tasks: List<TaskWithSubject>,
        filter: TaskFilter,
        subjectFilterId: Long?,
        query: String
    ): List<TaskWithSubject> {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val tomorrowStart = todayStart + (24 * 60 * 60 * 1000)
        val dayAfterTomorrowStart = tomorrowStart + (24 * 60 * 60 * 1000)

        return tasks.filter { item ->
            val matchesSubject = subjectFilterId == null || item.task.subjectId == subjectFilterId
            val matchesQuery = query.isBlank() ||
                    item.task.memorization.contains(query, ignoreCase = true) ||
                    item.task.homework.contains(query, ignoreCase = true) ||
                    item.task.notes.contains(query, ignoreCase = true) ||
                    (item.subject?.name?.contains(query, ignoreCase = true) == true)

            val matchesFilter = when (filter) {
                TaskFilter.ALL -> true
                TaskFilter.TODAY -> item.task.dueDateMillis in todayStart until tomorrowStart
                TaskFilter.TOMORROW -> item.task.dueDateMillis in tomorrowStart until dayAfterTomorrowStart
                TaskFilter.PENDING -> !item.task.isCompleted
                TaskFilter.COMPLETED -> item.task.isCompleted
            }

            matchesSubject && matchesQuery && matchesFilter
        }
    }

    fun setSelectedTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun setTaskFilter(filter: TaskFilter) {
        _uiState.update { it.copy(taskFilter = filter) }
    }

    fun setSubjectFilter(subjectId: Long?) {
        _uiState.update { it.copy(selectedSubjectFilterId = subjectId) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setThemeAccent(accent: com.example.ui.theme.ThemeAccent) {
        _uiState.update { it.copy(selectedThemeAccent = accent, userFeedbackMessage = "تم تغيير مظهر التطبيق إلى ${accent.title} 🎨") }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(userFeedbackMessage = null) }
    }

    // Task Actions
    fun saveTask(
        id: Long = 0,
        subjectId: Long,
        dueDateMillis: Long,
        targetDayOfWeek: Int,
        memorization: String,
        homework: String,
        notes: String,
        remindNightBefore: Boolean,
        remindDayBefore: Boolean,
        remindOneHourBefore: Boolean
    ) {
        viewModelScope.launch {
            val task = StudentTaskEntity(
                id = id,
                subjectId = subjectId,
                dueDateMillis = dueDateMillis,
                targetDayOfWeek = targetDayOfWeek,
                memorization = memorization.trim(),
                homework = homework.trim(),
                notes = notes.trim(),
                isCompleted = false,
                remindNightBefore = remindNightBefore,
                remindDayBefore = remindDayBefore,
                remindOneHourBefore = remindOneHourBefore
            )

            val taskId = if (id == 0L) {
                repository.insertTask(task)
            } else {
                repository.updateTask(task)
                id
            }

            // Schedule alarms for this task
            scheduleTaskReminders(taskId, task)

            _uiState.update {
                it.copy(userFeedbackMessage = "تم حفظ المطلوب وتفعيل التذكيرات بنجاح ✅")
            }
        }
    }

    fun toggleTaskCompletion(task: StudentTaskEntity) {
        viewModelScope.launch {
            val newState = !task.isCompleted
            repository.setTaskCompleted(task.id, newState)
            val msg = if (newState) "أحسنت! تم إنجاز المهمة بنجاح 🌟" else "تمت إعادة المهمة إلى قائمة الانتظار"
            _uiState.update { it.copy(userFeedbackMessage = msg) }
        }
    }

    fun deleteTask(task: StudentTaskEntity) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            // Cancel scheduled alarms
            NotificationHelper.cancelAlarm(context, (task.id * 10 + 1).toInt())
            NotificationHelper.cancelAlarm(context, (task.id * 10 + 2).toInt())
            NotificationHelper.cancelAlarm(context, (task.id * 10 + 3).toInt())

            repository.deleteTask(task)
            _uiState.update { it.copy(userFeedbackMessage = "تم حذف المطلوب") }
        }
    }

    // Subject Actions
    fun saveSubject(
        id: Long = 0,
        name: String,
        teacherName: String,
        colorHex: String,
        notes: String,
        scheduleSlots: List<Pair<Int, Pair<Int, Int>>> // dayOfWeek to (hour, minute)
    ) {
        viewModelScope.launch {
            val subject = SubjectEntity(
                id = id,
                name = name.trim(),
                teacherName = teacherName.trim(),
                colorHex = colorHex,
                notes = notes.trim()
            )

            val subjectId = if (id == 0L) {
                repository.insertSubject(subject)
            } else {
                repository.updateSubject(subject)
                id
            }

            // If updating, refresh slots
            if (id != 0L) {
                repository.deleteSlotsForSubject(subjectId)
            }

            // Insert slots
            scheduleSlots.forEach { (day, time) ->
                repository.insertSlot(
                    ScheduleSlotEntity(
                        subjectId = subjectId,
                        dayOfWeek = day,
                        hour = time.first,
                        minute = time.second
                    )
                )
            }

            _uiState.update {
                it.copy(userFeedbackMessage = "تم حفظ المادة والجدول بنجاح 📚")
            }
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
            _uiState.update { it.copy(userFeedbackMessage = "تم حذف المادة وجدولها") }
        }
    }

    fun addSlotToSubject(subjectId: Long, dayOfWeek: Int, hour: Int, minute: Int, location: String = "") {
        viewModelScope.launch {
            repository.insertSlot(
                ScheduleSlotEntity(
                    subjectId = subjectId,
                    dayOfWeek = dayOfWeek,
                    hour = hour,
                    minute = minute,
                    location = location
                )
            )
            _uiState.update { it.copy(userFeedbackMessage = "تمت إضافة موعد الحصة إلى الجدول") }
        }
    }

    fun deleteSlot(slot: ScheduleSlotEntity) {
        viewModelScope.launch {
            repository.deleteSlot(slot)
            _uiState.update { it.copy(userFeedbackMessage = "تم حذف موعد الحصة") }
        }
    }

    // Reminder Scheduling Logic
    private suspend fun scheduleTaskReminders(taskId: Long, task: StudentTaskEntity) {
        val context = getApplication<Application>()
        val subject = repository.getSubjectById(task.subjectId)
        val subjectName = subject?.name ?: "المادة"

        val taskSummary = buildString {
            if (task.memorization.isNotBlank()) append("📖 التسميع: ${task.memorization} ")
            if (task.homework.isNotBlank()) append("✍️ الواجب: ${task.homework}")
        }

        // 1. Night reminder (تسكير بالليل قبل موعد المادة)
        if (task.remindNightBefore && _uiState.value.isNightReminderEnabled) {
            val nightMillis = NotificationHelper.calculateNightReminderMillis(
                dueDateMillis = task.dueDateMillis,
                nightHour = _uiState.value.nightReminderHour,
                nightMinute = _uiState.value.nightReminderMinute
            )
            if (nightMillis > System.currentTimeMillis()) {
                NotificationHelper.scheduleAlarm(
                    context = context,
                    requestCode = (taskId * 10 + 1).toInt(),
                    triggerTimeMillis = nightMillis,
                    title = "🌙 تذكير ليلي: مراجعة $subjectName",
                    message = "حان وقت المذاكرة الليلية! مطلوباتك لمادة $subjectName:\n$taskSummary",
                    type = NotificationHelper.TYPE_NIGHT
                )
            }
        }

        // 2. Day before reminder (تذكير قبل اليوم بـ 24 ساعة)
        if (task.remindDayBefore) {
            val dayBeforeMillis = task.dueDateMillis - (24 * 60 * 60 * 1000)
            if (dayBeforeMillis > System.currentTimeMillis()) {
                NotificationHelper.scheduleAlarm(
                    context = context,
                    requestCode = (taskId * 10 + 2).toInt(),
                    triggerTimeMillis = dayBeforeMillis,
                    title = "📅 تذكير غداً: مادة $subjectName",
                    message = "لديك غداً مادة $subjectName. تأكد من إنجاز الواجب والتسميع:\n$taskSummary",
                    type = NotificationHelper.TYPE_DAY_BEFORE
                )
            }
        }

        // 3. One hour before reminder (تذكير قبل موعد المادة بساعة)
        if (task.remindOneHourBefore && _uiState.value.isOneHourBeforeEnabled) {
            // Find slot time for this subject on target day
            val slots = repository.getSlotsForSubject(task.subjectId)
            val matchingSlot = slots.find { it.dayOfWeek == task.targetDayOfWeek } ?: slots.firstOrNull()

            val classMillis = if (matchingSlot != null) {
                val cal = Calendar.getInstance().apply { timeInMillis = task.dueDateMillis }
                cal.set(Calendar.HOUR_OF_DAY, matchingSlot.hour)
                cal.set(Calendar.MINUTE, matchingSlot.minute)
                cal.set(Calendar.SECOND, 0)
                cal.timeInMillis
            } else {
                task.dueDateMillis
            }

            val oneHourBeforeMillis = classMillis - (60 * 60 * 1000)
            if (oneHourBeforeMillis > System.currentTimeMillis()) {
                NotificationHelper.scheduleAlarm(
                    context = context,
                    requestCode = (taskId * 10 + 3).toInt(),
                    triggerTimeMillis = oneHourBeforeMillis,
                    title = "⏰ موعد الحصة بعد ساعة: $subjectName",
                    message = "تبدأ حصة $subjectName قريباً! تفقد كتابك وواجبك الآن:\n$taskSummary",
                    type = NotificationHelper.TYPE_ONE_HOUR
                )
            }
        }
    }

    // Reminder Settings Updates
    fun updateNightReminderTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(nightReminderHour = hour, nightReminderMinute = minute) }
    }

    fun toggleTomorrowSummary(enabled: Boolean) {
        _uiState.update { it.copy(isTomorrowSummaryEnabled = enabled) }
    }

    fun toggleOneHourBefore(enabled: Boolean) {
        _uiState.update { it.copy(isOneHourBeforeEnabled = enabled) }
    }

    fun toggleNightReminder(enabled: Boolean) {
        _uiState.update { it.copy(isNightReminderEnabled = enabled) }
    }

    // Immediate Notification Testing for the Student
    fun triggerInstantTestNotification(type: String) {
        val context = getApplication<Application>()
        when (type) {
            "night" -> {
                NotificationHelper.showNotification(
                    context = context,
                    notificationId = 901,
                    title = "🌙 تذكير ليلي: مراجعة القرآن الكريم والرياضيات",
                    message = "حان موعد المذاكرة المسائية! راجع حفظ سورة الملك وحل تمارين الهندسة قبل النوم.",
                    type = NotificationHelper.TYPE_NIGHT
                )
            }
            "one_hour" -> {
                NotificationHelper.showNotification(
                    context = context,
                    notificationId = 902,
                    title = "⏰ الحصة بعد ساعة واحدة: اللغة العربية",
                    message = "تبدأ الحصة القادمة بعد ساعة (10:30 صباحاً). هل جهزت كشكول الواجب وقصيدة النصوص؟",
                    type = NotificationHelper.TYPE_ONE_HOUR
                )
            }
            "tomorrow_summary" -> {
                val tomorrowName = DayOfWeekHelper.getDayName(DayOfWeekHelper.getTomorrowAppDayNumber())
                NotificationHelper.showNotification(
                    context = context,
                    notificationId = 903,
                    title = "📋 ملخص جدول ومطلوبات الغد ($tomorrowName)",
                    message = "جدول غداً: رياضيات، لغة عربية، وعلوم. تذكّر إنهاء الواجبات والتسميع المحدد لتكون جاهزاً!",
                    type = NotificationHelper.TYPE_TOMORROW_SUMMARY
                )
            }
            else -> {
                NotificationHelper.showNotification(
                    context = context,
                    notificationId = 900,
                    title = "🔔 تجربة إشعار مهام الطالب",
                    message = "نظام التذكيرات والتنبيهات يعمل بنجاح وبشكل سليم! ستصلك التنبيهات في مواعيدها المحددة.",
                    type = "general"
                )
            }
        }
        _uiState.update { it.copy(userFeedbackMessage = "تم إرسال إشعار تجريبي فوري إلى هاتفك 🔔") }
    }

    /**
     * Finds the next upcoming class date & time for a subject based on its schedule slots
     */
    fun getNextUpcomingClassForSubject(subjectId: Long): Pair<Int, Long>? {
        val slots = _uiState.value.slots.filter { it.slot.subjectId == subjectId }
        if (slots.isEmpty()) return null

        val currentDay = DayOfWeekHelper.getTodayAppDayNumber()
        val cal = Calendar.getInstance()
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = cal.get(Calendar.MINUTE)

        // Try to find later today
        val todaySlot = slots.find {
            it.slot.dayOfWeek == currentDay &&
                    (it.slot.hour > currentHour || (it.slot.hour == currentHour && it.slot.minute > currentMinute))
        }

        if (todaySlot != null) {
            val targetCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, todaySlot.slot.hour)
                set(Calendar.MINUTE, todaySlot.slot.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return Pair(currentDay, targetCal.timeInMillis)
        }

        // Otherwise find upcoming day in week
        val sortedSlots = slots.sortedWith(
            compareBy {
                var diff = it.slot.dayOfWeek - currentDay
                if (diff <= 0) diff += 7
                diff
            }
        )

        val nextSlot = sortedSlots.firstOrNull() ?: return null
        var daysDiff = nextSlot.slot.dayOfWeek - currentDay
        if (daysDiff <= 0) daysDiff += 7

        val targetCal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, daysDiff)
            set(Calendar.HOUR_OF_DAY, nextSlot.slot.hour)
            set(Calendar.MINUTE, nextSlot.slot.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return Pair(nextSlot.slot.dayOfWeek, targetCal.timeInMillis)
    }
}
