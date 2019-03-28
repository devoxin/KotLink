package me.devoxin.kotlink.entities

interface IEventHook {

    public fun onTrackStart(player: AudioPlayer, track: AudioTrack)
    public fun onTrackEnd(player: AudioPlayer, track: AudioTrack, reason: String)
    public fun onTrackStuck(player: AudioPlayer, track: AudioTrack, thresholdMs: Long)
    public fun onTrackException(player: AudioPlayer, track: AudioTrack, exception: String)

}
