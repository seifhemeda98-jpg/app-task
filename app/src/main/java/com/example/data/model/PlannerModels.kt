package com.example.data.model

import com.example.data.local.entity.ScheduleSlotEntity
import com.example.data.local.entity.StudentTaskEntity
import com.example.data.local.entity.SubjectEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class TaskWithSubject(
    val task: StudentTaskEntity,
    val subject: SubjectEntity?
)

data class SlotWithSubject(
    val slot: ScheduleSlotEntity,
    val subject: SubjectEntity?
)

data class DailyScheduleSummary(
    val dayNumber: Int,
    val dayName: String,
    val slots: List<SlotWithSubject>,
    val tasks: List<TaskWithSubject>
)

object DayOfWeekHelper {
    // 1=السبت, 2=الأحد, 3=الإثنين, 4=الثلاثاء, 5=الأربعاء, 6=الخميس, 7=الجمعة
    val daysList = listOf(
        1 to "السبت",
        2 to "الأحد",
        3 to "الإثنين",
        4 to "الثلاثاء",
        5 to "الأربعاء",
        6 to "الخميس",
        7 to "الجمعة"
    )

    fun getDayName(dayNumber: Int): String {
        return daysList.find { it.first == dayNumber }?.second ?: "يوم غير محدد"
    }

    /**
     * Converts java.util.Calendar DAY_OF_WEEK into our representation:
     * Calendar.SATURDAY (7) -> 1
     * Calendar.SUNDAY (1) -> 2
     * Calendar.MONDAY (2) -> 3
     * Calendar.TUESDAY (3) -> 4
     * Calendar.WEDNESDAY (4) -> 5
     * Calendar.THURSDAY (5) -> 6
     * Calendar.FRIDAY (6) -> 7
     */
    fun getTodayAppDayNumber(): Int {
        val cal = Calendar.getInstance()
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> 1
            Calendar.SUNDAY -> 2
            Calendar.MONDAY -> 3
            Calendar.TUESDAY -> 4
            Calendar.WEDNESDAY -> 5
            Calendar.THURSDAY -> 6
            Calendar.FRIDAY -> 7
            else -> 1
        }
    }

    fun getTomorrowAppDayNumber(): Int {
        val today = getTodayAppDayNumber()
        return if (today == 7) 1 else today + 1
    }

    fun formatTime(hour: Int, minute: Int): String {
        val isAm = hour < 12
        val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val mStr = if (minute < 10) "0$minute" else "$minute"
        val suffix = if (isAm) "صباحاً" else "مساءً"
        return "$h:$mStr $suffix"
    }

    fun formatDateArabic(timeMillis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timeMillis }
        val todayCal = Calendar.getInstance()
        val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

        if (cal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
            cal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)
        ) {
            return "اليوم"
        }
        if (cal.get(Calendar.YEAR) == tomorrowCal.get(Calendar.YEAR) &&
            cal.get(Calendar.DAY_OF_YEAR) == tomorrowCal.get(Calendar.DAY_OF_YEAR)
        ) {
            return "غداً"
        }

        val sdf = SimpleDateFormat("EEEE d MMMM", Locale("ar"))
        return sdf.format(cal.time)
    }

    fun getUpcomingMillisForDayNumber(targetDay: Int, hour: Int = 9, minute: Int = 0): Long {
        val cal = Calendar.getInstance()
        val currentDay = getTodayAppDayNumber()
        var daysDiff = targetDay - currentDay
        if (daysDiff <= 0) {
            daysDiff += 7
        }
        cal.add(Calendar.DAY_OF_YEAR, daysDiff)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
