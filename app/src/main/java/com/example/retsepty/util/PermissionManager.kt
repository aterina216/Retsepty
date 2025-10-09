package com.example.retsepty.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlin.contracts.contract

class PermissionManager(private val context: Context) {
    fun hasStoragePermission(): Boolean{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            true
        }
        else{
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestStoragePermission(fragment: Fragment, onResult: (Boolean) -> Unit){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            fragment.requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION
            )
            permissionCallback = onResult
        }
        else{
            onResult(true)
        }
    }

    companion object {
        const val REQUEST_CODE_PERMISSION = 1001
        private var permissionCallback: ((Boolean) -> Unit)? = null
    }

    fun handlePermissionResult(requestCode: Int, grantResults: IntArray){
        if(requestCode == REQUEST_CODE_PERMISSION){
            val granted = grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
            permissionCallback?.invoke(granted)
            permissionCallback == null
        }
    }
}
