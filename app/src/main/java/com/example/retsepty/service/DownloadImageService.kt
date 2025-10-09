package com.example.retsepty.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.Continuation

class DownloadImageService : Service() {

    companion object {
        const val ACTION_START_DOWNLOAD = "START_DOWNLOAD"
        const val ACTION_DOWNLOAD_COMPLETE = "DOWNLOAD_COMPLETE"
        const val EXTRA_IMAGE_URL = "image_url"
        const val EXTRA_MEAL_NAME = "meal_name"

        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "download_channel"
    }

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("🟢 DownloadImageService ЗАПУЩЕН!")
        when (intent?.action) {
            ACTION_START_DOWNLOAD -> {
                val imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL) ?: ""
                val mealName = intent.getStringExtra(EXTRA_MEAL_NAME) ?: "Рецепт"
                // Запускаем в отдельном потоке
                Thread {
                    startDownLoad(imageUrl, mealName)
                }.start()
            }
        }
        return START_NOT_STICKY
    }

    private fun startDownLoad(imageUrl: String, mealName: String) {
        createNotificationChannel()

        try {
            // Сразу показываем уведомление
            showInitialNotification(mealName)

            // Реальное скачивание
            downloadAndSaveImage(imageUrl, mealName)

            // Завершение
            showDownloadCompleteNotification(mealName)

            sendBroadcast(Intent(ACTION_DOWNLOAD_COMPLETE).apply {
                putExtra("success", true)
                putExtra("meal_name", mealName)
            })

        } catch (e: Exception) {
            e.printStackTrace()
            sendBroadcast(Intent(ACTION_DOWNLOAD_COMPLETE).apply {
                putExtra("success", false)
                putExtra("error", e.message)
            })
            showDownloadErrorNotification(mealName, e.message ?: "Неизвестная ошибка")
        } finally {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun downloadAndSaveImage(imageUrl: String, mealName: String) {
        try {
            println("🔵 Начинаем скачивание: $imageUrl")

            // Скачиваем изображение
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connectTimeout = 30000
            connection.readTimeout = 30000

            val inputStream = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            connection.disconnect()

            if (bitmap == null) {
                throw Exception("Не удалось декодировать изображение")
            }

            println("🔵 Изображение скачано, сохраняем...")

            // Сохраняем в галерею
            saveToGallery(bitmap, mealName)

        } catch (e: Exception) {
            throw Exception("Ошибка при скачивании: ${e.message}")
        }
    }

    private fun saveToGallery(bitmap: Bitmap, mealName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveToGalleryQ(bitmap, mealName)
        } else {
            saveToGalleryLegacy(bitmap, mealName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToGalleryQ(bitmap: Bitmap, mealName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${mealName}_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Recipes")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            try {
                val outputStream = resolver.openOutputStream(it)
                outputStream?.use { stream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                        throw Exception("Не удалось сохранить изображение")
                    }
                }
                // Помечаем как завершенное
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)

                println("🟢 Изображение сохранено: $uri")
            } catch (e: Exception) {
                resolver.delete(uri, null, null)
                throw e
            }
        } ?: throw Exception("Не удалось создать файл")
    }

    @Suppress("DEPRECATION")
    private fun saveToGalleryLegacy(bitmap: Bitmap, mealName: String) {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val recipesDir = File(picturesDir, "Recipes")

        if (!recipesDir.exists() && !recipesDir.mkdirs()) {
            throw Exception("Не удалось создать папку Recipes")
        }

        val file = File(recipesDir, "${mealName}_${System.currentTimeMillis()}.jpg")

        try {
            FileOutputStream(file).use { outputStream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                    throw Exception("Не удалось сохранить изображение")
                }
            }

            // Обновляем галерею
            MediaScannerConnection.scanFile(
                this,
                arrayOf(file.absolutePath),
                arrayOf("image/jpeg"),
                null
            )

            println("🟢 Изображение сохранено: ${file.absolutePath}")
        } catch (e: Exception) {
            if (file.exists()) {
                file.delete()
            }
            throw e
        }
    }

    @SuppressLint("ForegroundServiceType")
    private fun showInitialNotification(mealName: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Сохранение изображения")
            .setContentText("$mealName - начинаем загрузку...")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .build()

        // Для Android 14+ указываем тип сервиса
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun showDownloadCompleteNotification(mealName: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Скачивание завершено")
            .setContentText("$mealName сохранен в галерею")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID + 1, notification)
    }

    private fun showDownloadErrorNotification(mealName: String, error: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Ошибка скачивания")
            .setContentText("$mealName: $error")
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID + 1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Загрузка изображений",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Показывает прогресс загрузки изображений"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null
}