package me.devoxin.kotlink.entities

import me.devoxin.kotlink.Client
import me.devoxin.kotlink.Node
import org.json.JSONObject

class AudioPlayer(
    public val client: Client,
    public val node: Node,
    public val guildId: Long
) {

    private var voiceUpdate = JSONObject()
    public var channelId: Long? = null
        private set

    public fun play(track: AudioTrack) {
        val payload = JSONObject(mapOf(
            "op" to "play",
            "guildId" to guildId.toString(),
            "track" to track.track
        ))
        node.send(payload)
    }

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