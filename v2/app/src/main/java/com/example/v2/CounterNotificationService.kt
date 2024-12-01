package com.example.v2

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

class CounterNotificationService(private val context : Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    fun showNotification(counter: Int) {
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Intent for handling button click
        val screenShotButtonIntent = Intent(context, CounterNotificationReceiver::class.java).apply {
            action = "ACTION_SCREENSHOT"
        }
        val screenShotButtonPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            screenShotButtonIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val deleteButtonIntent = Intent(context, CounterNotificationReceiver::class.java).apply {
            action = "ACTION_DELETE"
        }
        val deleteButtonPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            deleteButtonIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val cloudButtonIntent = Intent(context, CounterNotificationReceiver::class.java).apply {
            action = "ACTION_INCREMENT"
        }
        val cloudButtonPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            cloudButtonIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        // Inflate the custom layout
        val remoteViews = RemoteViews(context.packageName, R.layout.custom_notification_layout_one)
        // Set the updated counter value in the notification layout
        remoteViews.setTextViewText(R.id.textViewCounter, "Counter: $counter")
        // Set click action for the button
        remoteViews.setOnClickPendingIntent(R.id.screenShotButton, screenShotButtonPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.deletebutton, deleteButtonPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.cloudbutton, cloudButtonPendingIntent)

        // Build the notification
        val notification = NotificationCompat.Builder(context, COUNTER_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("PennyPeeper")
            .setContentText("Counter: $counter")
            .setAutoCancel(false)
            .setOngoing(true)
            .setCustomContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(activityPendingIntent)
            .build()

        notificationManager.notify(1, notification)
    }

    companion object{
        const val COUNTER_CHANNEL_ID = "counter_channel"
    }
}