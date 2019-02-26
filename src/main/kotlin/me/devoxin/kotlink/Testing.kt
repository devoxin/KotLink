package me.devoxin.kotlink

import me.devoxin.kotlink.interceptors.VoiceServerUpdateInterceptor
import me.devoxin.kotlink.interceptors.VoiceStateUpdateInterceptor
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.dv8tion.jda.core.entities.impl.JDAImpl
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

public fun main() {
    val client = Client("423858016413286400", 1)

    val j = JDABuilder()
        .setToken("NDIzODU4MDE2NDEzMjg2NDAw.D1dXtg.CY3Zu3wM9shpT5LM0Tc7WC-Po6I")
        .addEventListener(Shit(client))
        .build()

    val conf = NodeConfig("win10-local", "127.0.0.1", password = "youshallnotpass", region = "eu")
    client.addNode(conf)
}

class Shit(private val client: Client) : ListenerAdapter() {

    override fun onReady(event: ReadyEvent) {
        val handlers = (event.jda as JDAImpl).client.handlers
        handlers["VOICE_SERVER_UPDATE"] = VoiceServerUpdateInterceptor(client, event.jda as JDAImpl)
        handlers["VOICE_STATE_UPDATE"] = VoiceStateUpdateInterceptor(client, event.jda as JDAImpl)
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.idLong != 180093157554388993L) return

        val player = client.getPlayer(event.guild.idLong)

        if (event.message.contentRaw == "join") {
            (event.jda as JDAImpl).client.queueAudioConnect(event.member.voiceState.channel)
            return

        }

        client.getTracks("ytsearch:${event.message.contentRaw}").thenAccept {
            println(it.tracks)
            player.play(it.tracks[0])
            println("Playing ${it.tracks[0].title}")
        }
    }

}