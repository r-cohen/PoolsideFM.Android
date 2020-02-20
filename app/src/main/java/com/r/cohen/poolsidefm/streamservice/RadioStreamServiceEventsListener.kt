package com.r.cohen.poolsidefm.streamservice

interface RadioStreamServiceEventsListener {
    fun onPlayerStateChanged(playerState: PlayerState, audioSessionId: Int)
    fun onCurrentTrackInfoChanged(currentTrack: CurrentTrackModel)
}