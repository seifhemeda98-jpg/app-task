package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.notifications.NotificationHelper

class StudentPlannerApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
