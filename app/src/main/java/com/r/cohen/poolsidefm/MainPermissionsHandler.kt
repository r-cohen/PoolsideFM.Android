package com.r.cohen.poolsidefm

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainPermissionsHandler(
    var activity: Activity,
    var viewModel: MainViewModel
) {
    private val permissionsRequestCode = 1

    fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            viewModel.hasRecordAudioPermission = true
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), permissionsRequestCode)
        }
    }

    fun checkPermissionsWithoutRequest() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            viewModel.hasRecordAudioPermission = true
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionsRequestCode) {
            val recordAudioIndex = permissions.indexOf(Manifest.permission.RECORD_AUDIO)
            if (recordAudioIndex >= 0 && grantResults[recordAudioIndex] == PackageManager.PERMISSION_GRANTED) {
                viewModel.hasRecordAudioPermission = true
            }
        }
    }
}
