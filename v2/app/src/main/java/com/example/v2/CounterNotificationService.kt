package com.example.v2

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class CounterNotificationService(private val context : Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    fun showNotification(counter:Int){
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE,
            )
        val incrementIntent = PendingIntent.getBroadcast(context,2,Intent(context,CounterNotificationReceiver::class.java),PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, COUNTER_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_remove_red_eye_24)
            .setContentText("a counter = $counter")
            .setContentTitle("PennyPeeper")
            .setAutoCancel(false)
            .setContentIntent(activityPendingIntent)
            .addAction(R.drawable.baseline_remove_red_eye_24,"Increment",incrementIntent)
            .setOngoing(true)
            .build()

        notificationManager.notify(1,notification)
    }
    companion object{
        const val COUNTER_CHANNEL_ID = "counter_channel"
    }
}