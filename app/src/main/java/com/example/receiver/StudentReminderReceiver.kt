package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notifications.NotificationHelper

class StudentReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(NotificationHelper.EXTRA_NOTIFICATION_ID, 1001)
        val title = intent.getStringExtra(NotificationHelper.EXTRA_TITLE) ?: "تذكير مهام الطالب 📚"
        val message = intent.getStringExtra(NotificationHelper.EXTRA_MESSAGE) ?: "لديك مطلوبات ومذاكرة لمراجعتها الآن!"
        val type = intent.getStringExtra(NotificationHelper.EXTRA_REMINDER_TYPE) ?: "general"

        NotificationHelper.showNotification(
            context = context,
            notificationId = notificationId,
            title = title,
            message = message,
            type = type
        )
    }
}
