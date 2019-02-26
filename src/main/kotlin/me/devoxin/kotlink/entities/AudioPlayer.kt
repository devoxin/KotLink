package me.devoxin.kotlink.entities

import me.devoxin.kotlink.Client
import me.devoxin.kotlink.Node
import org.json.JSONObject

class AudioPlayer(
    private val client: Client,
    public val guildId: Long,
    public val node: Node
) {

    private var voiceUpdate = JSONObject()

    fun handleVoiceServerUpdate(content: JSONObject) {
        voiceUpdate.put("event", content)
        checkAndDispatch()
    }

    fun handleVoiceStateUpdate(content: JSONObject) {
        voiceUpdate.put("sessionId", content.getString("session_id"))
        checkAndDispatch()
    }

    fun checkAndDispatch() {
        if (voiceUpdate.has("event") && voiceUpdate.has("sessionId")) {
            voiceUpdate.put("op", "voiceUpdate")
            voiceUpdate.put("guildId", guildId.toString())
            node.send(voiceUpdate)
            voiceUpdate = JSONObject()
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