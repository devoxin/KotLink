package me.devoxin.kotlink.entities

import me.devoxin.kotlink.LavalinkClient
import me.devoxin.kotlink.Node
import org.json.JSONObject

class AudioPlayer(
    public val client: LavalinkClient,
    public val node: Node,
    public val guildId: Long
) {

    private var eventHook: IEventHook? = null

    private var voiceUpdate = JSONObject()
    public var channelId: Long? = null
        private set

    public var current: AudioTrack? = null
        private set

    public fun setListener(hook: IEventHook) {
        eventHook = hook
    }


    // +-------------------------+
    // | Actual player functions |
    // +-------------------------+

    public fun play(track: AudioTrack) {
        val payload = JSONObject(mapOf(
            "op" to "play",
            "guildId" to guildId.toString(),
            "track" to track.track
        ))

        node.send(payload)
        current = track

        onTrackStart(track)
    }

    public fun seek(milliseconds: Long) {
        val payload = JSONObject(mapOf(
            "op" to "seek",
            "guildId" to guildId.toString(),
            "position" to milliseconds // TODO: Checks
        ))
        node.send(payload)
    }


    // +-----------------------------+
    // | Boring event handling stuff |
    // +-----------------------------+

    internal fun onTrackStart(track: AudioTrack) {
        eventHook?.onTrackStart(this, track)
    }

    internal fun onTrackEnd(reason: String) {
        eventHook?.onTrackEnd(this, current!!, reason)
    }

    internal fun onTrackStuck(thresholdMs: Long) {
        eventHook?.onTrackStuck(this, current!!, thresholdMs)
    }

    internal fun onTrackException(exception: String) {
        eventHook?.onTrackException(this, current!!, exception)
    }

    // see Node.kt "handleEvent" for reasons why I use current. also tbh
    // I don't see any other cases where the track the event was emitted for
    // is not actually the currently playing one, like ?????? how would that work


    // +-----------------------------+
    // | Boring voice handling stuff |
    // +-----------------------------+

    internal fun handleVoiceServerUpdate(endpoint: String, token: String) {
        voiceUpdate.put("event", JSONObject(mapOf(
            "guild_id" to guildId.toString(),
            "token" to token,
            "endpoint" to endpoint
        )))
        checkAndDispatch()
    }

    internal fun handleVoiceStateUpdate(channelId: Long?, sessionId: String) {
        this.channelId = channelId

        if (channelId != null) {
            voiceUpdate.put("sessionId", sessionId)
            checkAndDispatch()
        } else {
            voiceUpdate = JSONObject()
        }
    }

    private fun checkAndDispatch() {
        if (voiceUpdate.has("event") && voiceUpdate.has("sessionId")) {
            voiceUpdate.put("op", "voiceUpdate")
            voiceUpdate.put("guildId", guildId.toString())
            node.send(voiceUpdate)
        }
    }

}