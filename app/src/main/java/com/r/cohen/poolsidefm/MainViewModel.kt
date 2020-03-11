package com.r.cohen.poolsidefm

import android.content.Context
import android.media.audiofx.Visualizer
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.r.cohen.poolsidefm.streamservice.CurrentTrackModel
import com.r.cohen.poolsidefm.streamservice.PlayerState
import com.r.cohen.poolsidefm.streamservice.RadioStreamServiceClient
import com.r.cohen.poolsidefm.streamservice.RadioStreamServiceEventsListener


data class MainViewModel(
    private val activity: MainActivity,
    private val streamClient: RadioStreamServiceClient
): BaseObservable(), RadioStreamServiceEventsListener {

    init {
        streamClient.listener = this
    }

    var streamInfo = CurrentTrackModel()
        @Bindable
        get() = field
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.streamInfo)
            }
        }

    var playerState = PlayerState.PREPARING
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.playerState)
        }

    private var audioSessionId = 0
    private lateinit var visualizer: Visualizer

    var visualizerBytes: ByteArray? = null
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.visualizerBytes)
        }

    var hasRecordAudioPermission: Boolean = false
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.hasRecordAudioPermission)
            updateVisualizer()
        }

    fun onStart(context: Context) = streamClient.connect(context)

    fun onStop(context: Context) {
        releaseVisualizer()
        streamClient.close(context)
    }

    fun onTogglePlayClick() {
        when (playerState) {
            PlayerState.STOPPED -> {
                playerState = PlayerState.PREPARING
                streamClient.togglePlayPause(true)
            }
            PlayerState.PREPARING,
            PlayerState.PLAYING -> {
                playerState = PlayerState.PREPARING
                streamClient.togglePlayPause(false)
            }
        }
    }

    fun activateVisualization() {
        activity.permissionsHandler.checkPermissions()
    }

    override fun onPlayerStateChanged(playerState: PlayerState, audioSessionId: Int) {
        Log.d("rafff", "onPlayerStateChanged $playerState")
        this.playerState = playerState
        this.audioSessionId = audioSessionId
        updateVisualizer()
    }

    override fun onCurrentTrackInfoChanged(currentTrack: CurrentTrackModel) {
        Log.d("rafff", "onCurrentTrackInfoChanged ${currentTrack.title}")
        this.streamInfo = currentTrack
    }

    private val visualizerListener = object: Visualizer.OnDataCaptureListener{
        override fun onFftDataCapture(p0: Visualizer?, p1: ByteArray?, p2: Int) {}
        override fun onWaveFormDataCapture(p0: Visualizer?, bytes: ByteArray?, p2: Int) {
            visualizerBytes = bytes
        }
    }

    private fun updateVisualizer() {
        if (hasRecordAudioPermission && this.playerState == PlayerState.PLAYING) {
            releaseVisualizer()
            visualizer = Visualizer(audioSessionId)
            visualizer.captureSize = Visualizer.getCaptureSizeRange()[1]
            visualizer.setDataCaptureListener(visualizerListener, Visualizer.getMaxCaptureRate() / 2, true, false)
            visualizer.enabled = true
        }
    }

    private fun releaseVisualizer() {
        if (::visualizer.isInitialized) {
            visualizer.release()
        }
    }
}

