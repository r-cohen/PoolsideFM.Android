package com.r.cohen.poolsidefm.streamservice

import com.google.gson.annotations.SerializedName

data class StreamInfoModel(
    @SerializedName("current_track")
    var currentTrack: CurrentTrackModel
)