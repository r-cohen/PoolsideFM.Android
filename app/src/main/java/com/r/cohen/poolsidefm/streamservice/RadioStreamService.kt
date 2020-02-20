package com.r.cohen.poolsidefm.streamservice

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.r.cohen.poolsidefm.R
import com.r.cohen.poolsidefm.extensions.launchPeriodicAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

const val STOP_INTENT_ACTION = "stopPlayingAction"
const val START_INTENT_ACTION = "startPlayingAction"

class RadioStreamService : Service() {
    private lateinit var binder: RadioStreamServiceBinder
    var playerState = PlayerState.PREPARING
        private set(value) {
            field = value
            listeners.forEach { it.onPlayerStateChanged(value, mediaPlayer.audioSessionId) }
        }
    var currentTrackInfo: CurrentTrackModel = CurrentTrackModel(
        title = "",
        artworkUrl = "https://images.radio.co/station_logos/s98f81d47e.jpg",
        artworkUrlLarge = "https://images.radio.co/station_logos/s98f81d47e.jpg")
        private set(value) {
            field = value
            listeners.forEach { it.onCurrentTrackInfoChanged(value) }
        }
    private val listeners = ArrayList<RadioStreamServiceEventsListener>()
    private lateinit var notifisFactory: RadioStreamNotificationFactory
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var streamInfoRepo: StreamInfoRepo
    private val trackInfoRefreshRate = 10 * 1000L
    private lateinit var fetchTrackInfoJob: Job
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        streamInfoRepo = StreamInfoRepo(getString(R.string.stream_status_url))
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        binder = RadioStreamServiceBinder(this)
        notifisFactory = RadioStreamNotificationFactory(this)
        mediaPlayer = getMediaPlayer()
        playerState = PlayerState.STOPPED
    }

    private fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
            setDataSource(getString(R.string.stream_url))
            setOnPreparedListener {
                Log.d("rafff", "media player prepared")
                Log.d("rafff", "playing...")
                it.start()
                notifisFactory.getNotification(this@RadioStreamService, currentTrackInfo) { notif ->
                    startForeground(NOTIFICATION_ID, notif)
                }
                playerState = PlayerState.PLAYING
            }
            setOnErrorListener { player, p1, p2 ->
                Log.d("rafff", "MediaPlayer.OnError $p1 $p2")
                cancelFetchStreamInfo()
                playerState = PlayerState.STOPPED
                player.release()
                mediaPlayer = getMediaPlayer()
                true
            }
            setOnBufferingUpdateListener { _, i ->
                Log.d("rafff", "MediaPlayer.OnBufferingUpdateListener $i")
            }
            setOnInfoListener { _, i, i2 ->
                Log.d("rafff", "MediaPlayer.OnInfoListener $i $i2")
                true
            }
            setOnSeekCompleteListener {
                Log.d("rafff", "MediaPlayer.OnSeekComplete")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            when (it) {
                STOP_INTENT_ACTION -> togglePlayPause(false)
                START_INTENT_ACTION -> togglePlayPause(true)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? = binder

    override fun onUnbind(intent: Intent?): Boolean {
        listeners.clear()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    fun togglePlayPause(play: Boolean) {
        mediaPlayer.let {
            if (!it.isPlaying && play) {
                fetchTrackInfoJob = CoroutineScope(Dispatchers.IO).launchPeriodicAsync(trackInfoRefreshRate) {
                    streamInfoRepo.getCurrentTrack()?.let { track ->
                        if (currentTrackInfo != track) {
                            currentTrackInfo = track
                            notifisFactory.getNotification(this@RadioStreamService, currentTrackInfo) { updatedNotif ->
                                notificationManager.notify(NOTIFICATION_ID, updatedNotif)
                            }
                        }
                    }
                }

                playerState = PlayerState.PREPARING
                it.prepareAsync()
            } else if (!play) {
                Log.d("rafff", "stopping...")
                cancelFetchStreamInfo()
                it.stop()
                stopForeground(true)
                playerState = PlayerState.STOPPED
            }
        }
    }

    private fun cancelFetchStreamInfo() {
        if (::fetchTrackInfoJob.isInitialized && !fetchTrackInfoJob.isCancelled) {
            fetchTrackInfoJob.cancel()
        }
    }

    fun addListener(listener: RadioStreamServiceEventsListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: RadioStreamServiceEventsListener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener)
        }
    }

    fun getAudioSessionId(): Int = mediaPlayer.audioSessionId
}