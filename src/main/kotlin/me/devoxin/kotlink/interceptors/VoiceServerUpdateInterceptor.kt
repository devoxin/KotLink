package me.devoxin.kotlink.interceptors

import me.devoxin.kotlink.Client
import org.json.JSONObject
import net.dv8tion.jda.core.entities.impl.JDAImpl
import net.dv8tion.jda.core.handle.SocketHandler

class VoiceServerUpdateInterceptor(
    private val client: Client,
    private val jda: JDAImpl
) : SocketHandler(jda) {

    override fun handleInternally(content: JSONObject): Long? {
        val idLong = content.getLong("guild_id")

        if (getJDA().guildSetupController.isLocked(idLong))
            return idLong

        // Get session
        val guild = getJDA().guildMap.get(idLong)
            ?: throw IllegalArgumentException("Attempted to start audio connection with Guild that doesn't exist! JSON: $content")

        client.players[idLong]?.handleVoiceServerUpdate(content)
        return null
    }
}