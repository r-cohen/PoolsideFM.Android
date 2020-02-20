package com.r.cohen.poolsidefm

import android.Manifest
import android.content.pm.PackageManager

class MainPermissionsHandler {
    companion object {
        const val permissionsRequestCode = 1
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Int {
        if (requestCode == permissionsRequestCode) {
            val recordAudioIndex = permissions.indexOf(Manifest.permission.RECORD_AUDIO)
            if (recordAudioIndex >= 0) {
                return grantResults[recordAudioIndex]
            }
        }
        return PackageManager.PERMISSION_DENIED
    }
}
