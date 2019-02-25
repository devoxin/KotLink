package me.devoxin.kotlink

import java.net.URI

public fun main() {
    val client = Client("249303797371895820", 42)
    val conf = NodeConfig("win10-local", "127.0.0.1", password = "youshallnotpass", region = "eu")
    client.addNode(conf)
}
