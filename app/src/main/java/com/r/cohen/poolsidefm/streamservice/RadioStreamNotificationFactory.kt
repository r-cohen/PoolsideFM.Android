package com.r.cohen.poolsidefm.streamservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.r.cohen.poolsidefm.MainActivity
import com.r.cohen.poolsidefm.R

const val NOTIFICATION_ID = 1
const val NOTIFICATION_CHANNEL_NAME = "RadioStreamServiceNotificationChannel"

class RadioStreamNotificationFactory(context: Context) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val goBackToAppIntent = PendingIntent.getActivity(context, 0, Intent(
        context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
    private val stopRadioIntent = Intent(context, RadioStreamService::class.java)

    init {
        stopRadioIntent.action = STOP_INTENT_ACTION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_NAME,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW)
        channel.enableVibration(false)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

    private fun build(context: Context, trackTitle: String, trackIcon: Bitmap? = null): Notification {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_NAME)
        trackIcon?.let { builder.setLargeIcon(it) }
        builder
            .setSmallIcon(R.drawable.ic_beach)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentIntent(goBackToAppIntent)
            .setContentText(trackTitle)

        val stopRadioPendingIntent = PendingIntent.getService(context, 0, stopRadioIntent, PendingIntent.FLAG_ONE_SHOT)
        builder.addAction(0, context.getString(R.string.stopRadioNotificationButton), stopRadioPendingIntent)
        return builder.build()
    }

    fun getNotification(context: Context, track: CurrentTrackModel, notifReady: (Notification) -> Unit) {
        Glide.with(context)
            .asBitmap()
            .load(track.artworkUrl)
            .into(object: CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {}
                override fun onResourceReady(
                    bmp: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    val updatedNotif = build(context, track.title, bmp)
                    notifReady(updatedNotif)
                }
            })
    }

}