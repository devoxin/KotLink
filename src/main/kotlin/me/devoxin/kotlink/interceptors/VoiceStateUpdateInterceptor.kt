package me.devoxin.kotlink.interceptors

import me.devoxin.kotlink.Client
import org.json.JSONObject
import net.dv8tion.jda.core.entities.impl.JDAImpl
import net.dv8tion.jda.core.handle.VoiceStateUpdateHandler


class VoiceStateUpdateInterceptor(
    private val client: Client,
    jda: JDAImpl
) : VoiceStateUpdateHandler(jda) {

    override fun handleInternally(content: JSONObject): Long? {
        val guildId: Long? = content.optLong("guild_id")

        if (guildId != null && jda.guildSetupController.isLocked(guildId))
            return guildId

        if (guildId == null)
            return super.handleInternally(content)

        val userId = content.getLong("user_id")
        val channelId: Long? = content.optLong("channel_id")
        val guild = jda.getGuildById(guildId) ?: return super.handleInternally(content)

        val member = guild.getMemberById(userId) ?: return super.handleInternally(content)

        // We only need special handling if our own state is modified
        if (member != guild.selfMember) return super.handleInternally(content)

        val channel = if (channelId != null) guild.getVoiceChannelById(channelId) else null

        client.players[guildId]?.handleVoiceStateUpdate(content)

        /*
        if (channelId == null) {
            // Null channel means disconnected
            if (link.getState() !== Link.State.DESTROYED) {
                link.onDisconnected()
            }
        } else if (channel != null) {
            link.setChannel(channel.id) // Change expected channel
        }
        */

        //if (link.getState() === Link.State.CONNECTED) {
        //    jda.client.updateAudioConnection(guildId, channel)
        //}

        return super.handleInternally(content)
    }
}