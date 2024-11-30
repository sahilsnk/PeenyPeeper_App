package com.example.v2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CounterNotificationReceiver : BroadcastReceiver() {
    private var temporary =  Counter.count
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "ACTION_INCREMENT") {
            // Increment the counter

            Counter.count++

            // Update the notification with the new counter value
            val service = CounterNotificationService(context)
            service.showNotification(Counter.count)
        }
        if (intent?.action == "ACTION_DELETE") {
            // Increment the counter
            Counter.count = 0

            // Update the notification with the new counter value
            val service = CounterNotificationService(context)
            service.showNotification(Counter.count)
        }
        if (intent?.action == "ACTION_STORE") {
            // Increment the counter
            temporary  = Counter.count

            // Update the notification with the new counter value
            val service = CounterNotificationService(context)
            service.showNotification(Counter.count)
        }
    }
}
