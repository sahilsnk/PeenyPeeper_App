package com.example.v1

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

@Suppress("UNREACHABLE_CODE")
class RunningService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
        return null
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Action.START.toString() -> {
                start()
            }
            Action.STOP.toString() -> {
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
        @SuppressLint("ForegroundServiceType")
        private fun start(){
            val notification = NotificationCompat.Builder(this, "running_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Run is active")
                .setContentText("Elapsed time: 00:50")
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()


            startForeground(1, notification)

        }


    enum class Action{
        START, STOP
    }
}