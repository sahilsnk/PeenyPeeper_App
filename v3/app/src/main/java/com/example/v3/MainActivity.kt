package com.example.v3

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStream

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_take_screenshot).setOnClickListener {
            try {
                takeScreenshot()
            } catch (e: Exception) {
                Log.e("ScreenshotError", "Error: ${e.message}")
                Toast.makeText(this, "Failed to take screenshot: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun takeScreenshot() {
        val rootView = window?.decorView?.rootView ?: run {
            Toast.makeText(this, "Unable to capture screenshot.", Toast.LENGTH_SHORT).show()
            return
        }
        rootView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveScreenshotForApi29AndAbove(bitmap)
        } else {
            saveScreenshotForApiBelow29(bitmap)
        }
    }

    private fun saveScreenshotForApi29AndAbove(bitmap: Bitmap) {
        val resolver: ContentResolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "screenshot_${System.currentTimeMillis()}.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/Screenshots")
        }
        try {
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            val outputStream: OutputStream? = uri?.let { resolver.openOutputStream(it) }
            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            Toast.makeText(this, "Screenshot saved successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save screenshot: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveScreenshotForApiBelow29(bitmap: Bitmap) {
        Toast.makeText(this, "This feature is designed for API 29+", Toast.LENGTH_SHORT).show()
    }
}
