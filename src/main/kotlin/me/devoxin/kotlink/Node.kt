package me.devoxin.kotlink

import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class Node(
    private val client: Client,
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
        println(message)
    }

    override fun onOpen(handshakeData: ServerHandshake) {
        println("connected")
    }

}