package com.r.cohen.poolsidefm.streamservice

import android.util.Log
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class StreamInfoRepo (baseUrl: String) {
    private val streamInfoService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(StreamInfoService::class.java)

    private fun getStreamInfo() = streamInfoService.getStreamInfo()

    fun getCurrentTrack(): CurrentTrackModel? {
        try {
            val result = getStreamInfo().execute()
            result.body()?.let { streamInfo ->
                return streamInfo.currentTrack
            }
        } catch (e: Exception) {
            Log.e("rafff", "failed fetching stream info")
        }
        return null
    }
}