package me.devoxin.kotlink

import me.devoxin.kotlink.entities.AudioPlayer
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

class Node(
    private val client: LavalinkClient,
    public val config: NodeConfig,
    public val headers: HashMap<String, String>
) : ReusableWebSocket(URI("ws://${config.address}:${config.wsPort}"), Draft_6455(), headers, 5000) {

    public val available
        get() = this.isOpen && !this.isClosing

    public val restUrl = "http://${config.address}:${config.restPort}"

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
                val guildId = json.getLong("guildId")
                val player = client.getPlayer(guildId) ?: return println("no player, the fuck?")
                handleEvent(json.getString("type"), player, json)
            }
        }
    }

    override fun onOpen(handshakeData: ServerHandshake) {
        println("connected")
    }

    fun handleEvent(type: String, player: AudioPlayer, json: JSONObject) {
        when (type) {
            "TrackEndEvent" -> {
                val track = json.getString("track")
                val reason = json.getString("reason")

                player.onTrackEnd(reason)
            }
        }
    }

}