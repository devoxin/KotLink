package me.devoxin.kotlink

import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor

class VoiceInterceptor(private val client: Client) : VoiceDispatchInterceptor {

    override fun onVoiceServerUpdate(update: VoiceDispatchInterceptor.VoiceServerUpdate) {
        val guildId = update.guildIdLong
        client.players[guildId]?.handleVoiceServerUpdate(update.endpoint, update.token)
    }

    override fun onVoiceStateUpdate(update: VoiceDispatchInterceptor.VoiceStateUpdate): Boolean {
        val guildId = update.guildIdLong
        client.players[guildId]?.handleVoiceStateUpdate(update.voiceState.sessionId)
        return true
    }

}