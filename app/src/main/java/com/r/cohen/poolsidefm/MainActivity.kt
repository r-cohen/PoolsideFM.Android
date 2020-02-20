package com.r.cohen.poolsidefm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.r.cohen.poolsidefm.databinding.ActivityMainBinding
import com.r.cohen.poolsidefm.streamservice.RadioStreamServiceClient


class MainActivity : AppCompatActivity() {
    private val streamServiceClient = RadioStreamServiceClient()
    private val viewModel = MainViewModel(streamServiceClient)
    private val permissionsHandler = MainPermissionsHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.viewModel = viewModel
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            viewModel.hasRecordAudioPermission = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), MainPermissionsHandler.permissionsRequestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val grant = permissionsHandler.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grant == PackageManager.PERMISSION_GRANTED) {
            viewModel.hasRecordAudioPermission = true
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop(this)
    }
}
