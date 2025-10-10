package com.example.retsepty.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.retsepty.util.GallerySaver
import com.example.retsepty.util.ImageDownloader
import com.example.retsepty.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageDownloadWorker(
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val imageDownloader = ImageDownloader()
    private val gallerySaver = GallerySaver(context)
    private val notificationHelper = NotificationHelper(context)

    override suspend fun doWork(): Result {
        return try {
            val imageUrl = params.inputData.getString("image_url") ?: return Result.failure()
            val mealName = params.inputData.getString("meal_name") ?: "Рецепт"

            // Используем отдельные классы
            val bitmap = imageDownloader.downloadImage(imageUrl)
            gallerySaver.saveImage(bitmap, mealName)
            notificationHelper.showNotification("$mealName сохранен в галерею")

            Result.success()
        } catch (e: Exception) {
            notificationHelper.showNotification("Ошибка: ${e.message}")
            Result.failure()
        }
    }
}