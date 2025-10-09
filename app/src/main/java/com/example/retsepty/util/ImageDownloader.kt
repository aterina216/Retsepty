package com.example.retsepty.util

import androidx.fragment.app.Fragment
import android.content.Context
import android.widget.Toast

class ImageDownloader(private val context: Context) {

    private val permissionManager = PermissionManager(context)
    private val downloadStarter = DownloadStarter(context)

    fun downloadImage(imageUrl: String, mealName: String, fragment: Fragment){

        println("🟢 ImageDownloader.start()")

        if(permissionManager.hasStoragePermission()){
            println("🟢 Разрешения есть, запускаем скачивание")
            startDownload(imageUrl, mealName)
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
                startDownload(imageUrl, mealName)
            }
            else{
                showPermissionDeniedMessage()
            }
        }
    }

    private fun startDownload(imageUrl: String, mealName: String){
        downloadStarter.startImageDownload(imageUrl, mealName)
        showDownloadStartedMessage()
    }

    private fun showDownloadStartedMessage(){
        Toast.makeText(context, "Скачивание началось...", Toast.LENGTH_SHORT).show()
    }

    private fun showPermissionDeniedMessage(){
        Toast.makeText(context, "Нужно разрешение для сохранения", Toast.LENGTH_LONG).show()
    }
}