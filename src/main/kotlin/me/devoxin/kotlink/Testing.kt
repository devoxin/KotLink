package me.devoxin.kotlink

import java.net.URI

public fun main(args: Array<String>) {
    val client = Client("249303797371895820", 42)

    client.addNode("win10-local", URI("ws://127.0.0.1:2333"), "youshallnotpass", "eu")
}
