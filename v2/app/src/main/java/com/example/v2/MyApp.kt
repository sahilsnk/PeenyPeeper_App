package com.example.v2

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    private fun createNotificationChannel(){
        val channel = NotificationChannel(CounterNotificationService.COUNTER_CHANNEL_ID,"channel_name",NotificationManager.IMPORTANCE_HIGH)
        channel.description = "to make interactive"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)


    }
}
