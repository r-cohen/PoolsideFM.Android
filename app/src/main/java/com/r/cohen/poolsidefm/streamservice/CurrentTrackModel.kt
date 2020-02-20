package com.r.cohen.poolsidefm.streamservice

import com.google.gson.annotations.SerializedName

data class CurrentTrackModel(
    @SerializedName("title")
    val title: String = "",
    @SerializedName("artwork_url")
    val artworkUrl: String = "",
    @SerializedName("artwork_url_large")
    val artworkUrlLarge: String = ""
)