package com.r.cohen.poolsidefm.streamservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

class RadioStreamServiceClient {
    var listener: RadioStreamServiceEventsListener? = null
    private var service: RadioStreamService? = null

    private val serviceConnection = object: ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            service = null
        }

        override fun onServiceConnected(p0: ComponentName?, serviceBinder: IBinder?) {
            val binder = serviceBinder as RadioStreamServiceBinder
            service = binder.streamService
            service?.let { streamService ->
                listener?.let {
                    it.onPlayerStateChanged(streamService.playerState, streamService.getAudioSessionId())
                    it.onCurrentTrackInfoChanged(streamService.currentTrackInfo)
                    streamService.addListener(it)
                }
            }
        }
    }

    fun close(context: Context) {
        listener?.let {
            service?.removeListener(it)
        }
        context.unbindService(serviceConnection)
    }

    fun connect(context: Context) {
        val intent = Intent(context, RadioStreamService::class.java)
        context.startService(intent)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun togglePlayPause(play: Boolean) = service?.togglePlayPause(play)
}