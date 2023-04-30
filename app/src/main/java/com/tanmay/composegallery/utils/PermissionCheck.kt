package com.tanmay.composegallery.utils

import android.Manifest.permission.*
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PermissionCheck(private val context: Context, private val activityResultContracts: ActivityResultLauncher<Array<String>>) {
    private fun checkPermission(permissionList: List<String>): Boolean {
        if (context !is Activity) return false

        val needRequestPermissionList = permissionList
            .map { it to ContextCompat.checkSelfPermission(context, it) }
            .filter { it.second != PackageManager.PERMISSION_GRANTED }
            .map { it.first }
            .toTypedArray()

        return if (needRequestPermissionList.isEmpty()) {
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    needRequestPermissionList.first()
                )
            ) {
                activityResultContracts.launch(needRequestPermissionList)
            } else {
                activityResultContracts.launch(needRequestPermissionList)
            }
            false
        }
    }

    fun checkStoragePermission(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
                checkStoragePermissionUnderAPI33()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                checkStoragePermissionOrHigherAPI33()
            }
            else -> true
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun checkStoragePermissionUnderAPI33(): Boolean {
        return checkPermission(arrayListOf(READ_EXTERNAL_STORAGE))
    }

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    fun checkStoragePermissionOrHigherAPI33(): Boolean {
        return checkPermission(arrayListOf(READ_MEDIA_IMAGES))
    }

    fun showPermissionDialog() {
        Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
    }

}