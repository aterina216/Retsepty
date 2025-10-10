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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadImageWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext,
    params
) {

    companion object {
        const val KEY_IMAGE_URL = "image_url"
        const val KEY_MEAL_NAME = "meal_name"
        private const val CHANNEL_ID = "download_channel"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        try {
            val imageUrl = inputData.getString(KEY_IMAGE_URL) ?: return@withContext Result.failure()
            val mealName = inputData.getString(KEY_MEAL_NAME) ?: "Рецепт"
            val bitmap = downloadImage(imageUrl)
            saveToGallery(bitmap, mealName)
            showSuccessNotification(mealName)
            Result.success()
        }
        catch (e: Exception){
            e.printStackTrace()
            showErrorNotification(e.message ?: "Ошибка скачивания")
            Result.failure()
        }

    }

    private fun showErrorNotification(message: String){
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
        as NotificationManager

        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Ошибка скачивания")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Загрузка изображений",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о скачивании изображений"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private suspend fun downloadImage(imageUrl: String): Bitmap{
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 30000
        connection.readTimeout = 30000

        return try{
            val inputStream = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            bitmap?: throw Exception("Не удалось декодировать изображение")
        }
        finally {
            connection.disconnect()
        }
    }

    private fun saveToGallery(bitmap: Bitmap, mealName: String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            saveToGalleryQ(bitmap, mealName)
        }
        else{
            saveToGalleryLegasy(bitmap, mealName)
        }
    }

    private fun saveToGalleryLegasy(bitmap: Bitmap, mealName: String){
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val recipesDir = File(picturesDir, "Recipes")

        if(!recipesDir.exists() && !recipesDir.mkdirs()){
            throw Exception("Не удалось создать папку Recipes")
        }

        val file = File(recipesDir, "${mealName}_${System.currentTimeMillis()}.jpg")

        FileOutputStream(file).use {
            outputStream ->
            if(!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)){
                throw Exception("Не удалось сохранить изображение")
            }
        }

        MediaScannerConnection.scanFile(
            applicationContext,
            arrayOf(file.absolutePath),
            arrayOf("image/jpeg"),
            null
        )
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToGalleryQ(bitmap: Bitmap, mealName: String){
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${mealName}_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Recipes")
        }

        val resolver = applicationContext.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { stream ->
                if(!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)){
                    throw Exception("Не удалось сохранить изображение")
                }
            }
        } ?: throw Exception("Не удалось создать файл")
    }

    private fun showSuccessNotification(mealName: String){
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
        as NotificationManager

        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Скачивание завершено")
            .setContentText("$mealName сохранен в галерею")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}