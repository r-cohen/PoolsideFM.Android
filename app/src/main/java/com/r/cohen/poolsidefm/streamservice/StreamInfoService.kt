package com.r.cohen.poolsidefm.streamservice

import retrofit2.Call
import retrofit2.http.GET

interface StreamInfoService {
    @GET("stations/s98f81d47e/status")
    fun getStreamInfo(): Call<StreamInfoModel>
}