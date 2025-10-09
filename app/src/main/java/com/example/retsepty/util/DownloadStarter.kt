package com.example.retsepty.util

import android.content.Context
import android.content.Intent
import com.example.retsepty.service.DownloadImageService

class DownloadStarter(private val context: Context) {
    fun startImageDownload(imageUrl: String, mealName: String){
        val intent = Intent(context, DownloadImageService::class.java).apply {
            action = DownloadImageService.ACTION_START_DOWNLOAD
            putExtra(DownloadImageService.EXTRA_IMAGE_URL, imageUrl)
            putExtra(DownloadImageService.EXTRA_MEAL_NAME, mealName)
        }
        context.startService(intent)
    }
}
