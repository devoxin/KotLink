package me.devoxin.kotlink

import me.devoxin.kotlink.entities.AudioPlayer
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor

class VoiceInterceptor(private val client: Client<AudioPlayer>) : VoiceDispatchInterceptor {

    override fun onVoiceServerUpdate(update: VoiceDispatchInterceptor.VoiceServerUpdate) {
        val guildId = update.guildIdLong
        client.players[guildId]?.handleVoiceServerUpdate(update.endpoint, update.token)
    }

    override fun onVoiceStateUpdate(update: VoiceDispatchInterceptor.VoiceStateUpdate): Boolean {
        val guildId = update.guildIdLong
        val voiceState = update.voiceState

        client.players[guildId]?.handleVoiceStateUpdate(voiceState.channel?.idLong, voiceState.sessionId)
        return true
    }

}