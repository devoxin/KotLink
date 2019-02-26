package me.devoxin.kotlink

import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import org.slf4j.LoggerFactory

import java.net.InetSocketAddress
import java.net.URI

abstract class ReusableWebSocket(
    private val serverUri: URI,
    private val draft: Draft,
    private val headers: Map<String, String>,
    private val connectTimeout: Int
) {

    private var socket: DisposableSocket? = null
    private var isUsed = false

    val remoteSocketAddress: InetSocketAddress?
        get() = socket?.remoteSocketAddress

    val isOpen: Boolean
        get() = socket != null && socket!!.isOpen

    val isConnecting: Boolean
        get() = socket != null && socket!!.isConnecting

    val isClosed: Boolean
        get() = socket == null || socket!!.isClosed

    val isClosing: Boolean
        get() = socket != null && socket!!.isClosing

    abstract fun onOpen(handshakeData: ServerHandshake)

    abstract fun onMessage(message: String)

    abstract fun onClose(code: Int, reason: String, remote: Boolean)

    abstract fun onError(ex: Exception)

    fun send(text: String) {
        if (isOpen) {
            socket!!.send(text)
        }
    }

    fun send(json: JSONObject) = send(json.toString())

    fun connect() {
        if (socket == null || isUsed) {
            socket = DisposableSocket(serverUri, draft, headers, connectTimeout)
        }

        socket!!.connect()
        isUsed = true
    }

    @Throws(InterruptedException::class)
    fun connectBlocking() {
        if (socket == null || isUsed) socket = DisposableSocket(serverUri, draft, headers, connectTimeout)
        socket!!.connectBlocking()
        isUsed = true
    }

    fun close() = socket?.close()

    fun close(code: Int) = socket?.close(code)

    fun close(code: Int, reason: String) = socket?.close(code, reason)

    private inner class DisposableSocket internal constructor(
        serverUri: URI,
        protocolDraft: Draft,
        httpHeaders: Map<String, String>,
        connectTimeout: Int
    ) : WebSocketClient(serverUri, protocolDraft, httpHeaders, connectTimeout) {

        init {
            isUsed = false
        }

        override fun onOpen(handshakedata: ServerHandshake) {
            this@ReusableWebSocket.onOpen(handshakedata)
        }

        override fun onMessage(message: String) {
            this@ReusableWebSocket.onMessage(message)
        }

        override fun onClose(code: Int, reason: String, remote: Boolean) {
            this@ReusableWebSocket.onClose(code, reason, remote)
        }

        override fun onError(ex: Exception) {
            this@ReusableWebSocket.onError(ex)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ReusableWebSocket::class.java)
    }

}