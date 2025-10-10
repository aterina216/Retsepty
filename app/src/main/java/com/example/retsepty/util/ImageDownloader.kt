package com.example.retsepty.util

import androidx.fragment.app.Fragment
import android.content.Context
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.retsepty.service.DownloadImageWorker

class ImageDownloader(private val context: Context) {

    private val permissionManager = PermissionManager(context)

    fun downloadImage(imageUrl: String, mealName: String, fragment: Fragment){

        println("🟢 ImageDownloader: запуск через WorkManager")

        if(permissionManager.hasStoragePermission()){
            println("🟢 Разрешения есть, запускаем скачивание")
            startDownloadWithWorkManager(imageUrl, mealName)
        }
        else{
            println("🟡 Запрашиваем разрешения")
            requestPermissionAndDownload(imageUrl, mealName, fragment)
        }
    }

    private fun requestPermissionAndDownload(imageUrl: String, mealName: String, fragment: Fragment){
        permissionManager.requestStoragePermission(fragment){
            granted ->
            if(granted){
                startDownloadWithWorkManager(imageUrl, mealName)
            }
            else{
                showPermissionDeniedMessage()
            }
        }
    }

    private fun startDownloadWithWorkManager(imageUrl: String, mealName: String){
        val inputData = workDataOf(DownloadImageWorker.KEY_IMAGE_URL to imageUrl,
            DownloadImageWorker.KEY_MEAL_NAME to mealName)

        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadImageWorker>()
            .setInputData(inputData)
            .addTag("image_download")
            .build()

        WorkManager.getInstance(context).enqueue(downloadWorkRequest)

        showDownloadStartedMessage()
        println("🟢 WorkManager запущен для: $mealName")
    }

    private fun showDownloadStartedMessage(){
        Toast.makeText(context, "Скачивание началось...", Toast.LENGTH_SHORT).show()
    }

    private fun showPermissionDeniedMessage(){
        Toast.makeText(context, "Нужно разрешение для сохранения", Toast.LENGTH_LONG).show()
    }
}