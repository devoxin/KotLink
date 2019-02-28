package me.devoxin.kotlink.entities

import me.devoxin.kotlink.Client
import me.devoxin.kotlink.Node
import org.json.JSONObject

class AudioPlayer(
    private val client: Client,
    public val guildId: Long,
    public val node: Node
) {

    private val lastSessionId: String? = null
    private val channelId: Long? = null
    private var voiceUpdate = JSONObject()

    fun handleVoiceServerUpdate(endpoint: String, token: String) {
        voiceUpdate.put("event", JSONObject(mapOf(
            "guild_id" to guildId.toString(),
            "token" to token,
            "endpoint" to endpoint
        )))
        checkAndDispatch()
    }

    fun handleVoiceStateUpdate(sessionId: String) {
        println(voiceUpdate)
        voiceUpdate.put("sessionId", sessionId)
        checkAndDispatch()
    }

    fun checkAndDispatch() {
        if (voiceUpdate.has("event") && voiceUpdate.has("sessionId")) {
            voiceUpdate.put("op", "voiceUpdate")
            voiceUpdate.put("guildId", guildId.toString())
            node.send(voiceUpdate)
            //voiceUpdate = JSONObject()
        }
    }

    fun play(track: AudioTrack) {
        val payload = JSONObject(mapOf(
            "op" to "play",
            "guildId" to guildId.toString(),
            "track" to track.track
        ))
        node.send(payload)
    }

}