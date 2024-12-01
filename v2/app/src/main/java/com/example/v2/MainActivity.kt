package com.example.v2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var screenCapturePermissionLauncher: ActivityResultLauncher<Intent>

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 100
        private const val TAG = "com.example.v2.MainActivity"
        var screenCaptureIntent: Intent? = null
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize MediaProjectionManager
        mediaProjectionManager = getSystemService(MediaProjectionManager::class.java)

        // Register the screen capture permission launcher
        screenCapturePermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Save the permission result intent for future use
                screenCaptureIntent = result.data
                Log.d(TAG, "Screen capture permission granted")
            } else {
                Log.e(TAG, "Screen capture permission denied")
            }
        }

        // Enable edge-to-edge UI
        enableEdgeToEdge()

        // Set the content view
        setContentView(R.layout.activity_main)

        // Permissions to request
        val permissions = arrayOf(
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.FOREGROUND_SERVICE,
            android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION
        )

        ActivityCompat.requestPermissions(this, permissions, NOTIFICATION_PERMISSION_REQUEST_CODE)

        // Request screen capture permission
        requestScreenCapturePermission()

        // Initialize and show the notification service
        val service = CounterNotificationService(applicationContext)
        service.showNotification(Counter.count)
    }

    private fun requestScreenCapturePermission() {
        val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
        screenCapturePermissionLauncher.launch(permissionIntent)
    }
}