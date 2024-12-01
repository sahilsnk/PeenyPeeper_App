package com.example.v2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class CounterNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            "ACTION_SCREENSHOT" -> {
                // Start the com.example.v2.com.example.v2.ScreenshotService

                val screenshotIntent = Intent(context, ScreenshotService::class.java)
                context.startService(screenshotIntent)
                Log.d(this.javaClass.simpleName, "onReceive: done")
            }
            "ACTION_INCREMENT" -> {
                Counter.count++
                // Handle increment action
            }
            "ACTION_DELETE" -> {
                Counter.count = 0
                val service = CounterNotificationService(context)
                service.showNotification(Counter.count)
            }
            "ACTION_STORE" -> {
                val temporary = Counter.count
                val service = CounterNotificationService(context)
                service.showNotification(temporary)
            }
        }
    }
}