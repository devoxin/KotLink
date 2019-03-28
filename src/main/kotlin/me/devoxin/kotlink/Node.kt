package me.devoxin.kotlink

import me.devoxin.kotlink.entities.AudioPlayer
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.net.URI

class Node(
    private val client: LavalinkClient,
    public val config: NodeConfig,
    public val headers: Map<String, String>
) : ReusableWebSocket(URI("ws://${config.address}:${config.wsPort}"), Draft_6455(), headers, 5000) {

    public val log = LoggerFactory.getLogger(Node::class.java)!!

    public val available
        get() = this.isOpen && !this.isClosing

    public val restUrl = "http://${config.address}:${config.restPort}"


    override fun onOpen(handshakeData: ServerHandshake) {
        log.info("Successfully connected to Lavalink node ${config.name}")
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {

    }

    override fun onError(ex: Exception) {
        throw NodeException(ex.localizedMessage) // todo log
    }

    override fun onMessage(message: String) {
        val json = JSONObject(message)
        val op = json.getString("op")

        when (op) {
            "event" -> {
                val eventType = json.getString("type")
                val guildId = json.getLong("guildId")
                val player = client.getPlayer(guildId)
                    ?: return log.warn("Received event for uncached player, guildId: $guildId, type: $eventType")
                handleEvent(eventType, player, json)
            }
        }
    }

    fun handleEvent(type: String, player: AudioPlayer, json: JSONObject) {
        //val track = json.getString("track")
        // tbh an attempt was made to decode the base64 track string
        // but it doesn't include all of the fields AudioTrack has
        // because some of them are filled in by other classes only found in
        // lavaplayer. and I don't want to add LP as a dependency because my intentions are
        // to keep this wrapper minimal and lightweight.

        when (type) {
            "TrackEndEvent" -> {
                val reason = json.getString("reason")
                player.onTrackEnd(reason)
            }
            "TrackExceptionEvent" -> {
                val error = json.getString("error")
                player.onTrackException(error)
            }
            "TrackStuckEvent" -> {
                val thresholdMs = json.getLong("thresholdMs")
                player.onTrackStuck(thresholdMs)
            }
        }
    }

}