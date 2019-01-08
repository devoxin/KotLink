package me.devoxin.kotlink

import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class Node(
    private val client: Client,
    public val name: String,
    public val region: String,

    public val srvUri: URI,
    public val headers: HashMap<String, String>

) : ReusableWebSocket(srvUri, Draft_6455(), headers, 5000) {

    public var available: Boolean = false
        private set


    override fun onClose(code: Int, reason: String, remote: Boolean) {
    }

    override fun onError(ex: Exception) {
    }

    override fun onMessage(message: String) {
    }

    override fun onOpen(handshakeData: ServerHandshake) {
        println("connected")
    }

}