package com.example.v2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.MediaScannerConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScreenshotService : Service() {
    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null

    companion object {
        private const val SCREENSHOT_FOLDER = "AutoScreenshots"
        private const val NOTIFICATION_CHANNEL_ID = "ScreenshotServiceChannel"
        private const val NOTIFICATION_ID = 1001
        private const val TAG = "com.example.v2.ScreenshotService"
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Screenshot Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start as a foreground service
        val notification = createForegroundNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Capture screenshot
        captureScreenshot()
        return START_NOT_STICKY
    }

    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Taking Screenshot")
            .setContentText("Capturing screen...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun captureScreenshot() {
        // Get the saved screen capture intent from com.example.v2.MainActivity
        val savedIntent = MainActivity.screenCaptureIntent

        if (savedIntent == null) {
            Log.e(TAG, "No screen capture intent available")
            stopSelf()
            return
        }

        val mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        // Use the saved intent to get MediaProjection
        mediaProjection = mediaProjectionManager.getMediaProjection(
            android.app.Activity.RESULT_OK,
            savedIntent
        )

        // Register a callback to manage MediaProjection lifecycle
        mediaProjection?.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
                Log.d(TAG, "MediaProjection stopped")
                cleanupResources()
            }
        }, null)

        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        // Create ImageReader to capture screenshot
        imageReader = ImageReader.newInstance(
            width,
            height,
            PixelFormat.RGBA_8888,
            1
        )

        try {
            // Create virtual display
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "ScreenCapture",
                width,
                height,
                displayMetrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader?.surface,
                null,
                null
            )

            // Capture and process screenshot
            imageReader?.setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage()
                if (image != null) {
                    try {
                        val bitmap = convertImageToBitmap(image)
                        saveScreenshot(bitmap)
                        processAndDeleteScreenshot(bitmap)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing screenshot", e)
                    } finally {
                        image.close()
                    }
                }
            }, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating virtual display", e)
            cleanupResources()
        }
    }

    private fun convertImageToBitmap(image: Image): Bitmap {
        val plane = image.planes[0]
        val buffer = plane.buffer
        val pixelStride = plane.pixelStride
        val rowStride = plane.rowStride
        val rowPadding = rowStride - pixelStride * image.width

        val bitmap = Bitmap.createBitmap(
            image.width + rowPadding / pixelStride,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        return bitmap
    }

    private fun saveScreenshot(bitmap: Bitmap) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            SCREENSHOT_FOLDER
        )

        // Create the storage directory if it doesn't exist
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val imageFile = File(storageDir, "SCREENSHOT_$timeStamp.jpg")

        try {
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }

            // Scan the file so it shows up in gallery
            MediaScannerConnection.scanFile(
                applicationContext,
                arrayOf(imageFile.toString()),
                null,
                null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error saving screenshot", e)
        }
    }

    private fun processAndDeleteScreenshot(bitmap: Bitmap) {
        // Add your custom processing logic here
        Log.d(TAG, "Screenshot processed")

        // Clean up resources
        bitmap.recycle()
        cleanupResources()
    }

    private fun cleanupResources() {
        Log.d(TAG, "Cleaning up resources")
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()

        // Stop the foreground service and stopSelf
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanupResources()
    }
}