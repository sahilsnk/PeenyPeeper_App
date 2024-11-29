package com.example.v1

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var c  = 10
        // Request notification permissions

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }


        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Start Service Button
        val startButton = findViewById<Button>(R.id.start_button)
        startButton.setOnClickListener {
            Intent(applicationContext, RunningService::class.java).also {
                it.action = RunningService.Action.START.toString()
                startForegroundService(it)
            }
        }

        // Stop Service Button
        val stopButton = findViewById<Button>(R.id.stop_button)
        stopButton.setOnClickListener {
            Intent(applicationContext, RunningService::class.java).also {
                it.action = RunningService.Action.STOP.toString()
                startForegroundService(it)
            }
        }

        // Handle system bar insets

    }
}
