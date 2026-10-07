package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.receiver.StudentReminderReceiver
import java.util.Calendar

object NotificationHelper {

    const val CHANNEL_ID = "student_tasks_reminders_channel"
    const val CHANNEL_NAME = "تذكيرات مهام وجدول الطالب"
    const val CHANNEL_DESC = "إشعارات تذكير المذاكرة الليلية، الحصص القادمة، ومطلوبات الغد"

    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_MESSAGE = "extra_message"
    const val EXTRA_REMINDER_TYPE = "extra_reminder_type"

    const val TYPE_NIGHT = "type_night"
    const val TYPE_ONE_HOUR = "type_one_hour"
    const val TYPE_DAY_BEFORE = "type_day_before"
    const val TYPE_TOMORROW_SUMMARY = "type_tomorrow_summary"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        type: String = "general"
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    fun scheduleAlarm(
        context: Context,
        requestCode: Int,
        triggerTimeMillis: Long,
        title: String,
        message: String,
        type: String
    ) {
        // If trigger time is in the past, do not schedule past alarm
        if (triggerTimeMillis <= System.currentTimeMillis()) {
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, StudentReminderReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ID, requestCode)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_REMINDER_TYPE, type)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // Fallback for cases where exact alarm permission is restricted
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        }
    }

    fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, StudentReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    /**
     * Helper to compute night reminder trigger time (e.g. 8:30 PM on the night before the target date)
     */
    fun calculateNightReminderMillis(dueDateMillis: Long, nightHour: Int = 20, nightMinute: Int = 30): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = dueDateMillis }
        cal.add(Calendar.DAY_OF_YEAR, -1) // Night before
        cal.set(Calendar.HOUR_OF_DAY, nightHour)
        cal.set(Calendar.MINUTE, nightMinute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        // If that is already in the past, maybe schedule for today night if before class
        if (cal.timeInMillis < System.currentTimeMillis() && dueDateMillis > System.currentTimeMillis()) {
            val todayNight = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, nightHour)
                set(Calendar.MINUTE, nightMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (todayNight.timeInMillis > System.currentTimeMillis()) {
                return todayNight.timeInMillis
            }
        }
        return cal.timeInMillis
    }
}
