package com.example.v1

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class RunningApp : Application() {

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "running_channel",  // Unique ID for the channel
                "Running Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for active running service"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
