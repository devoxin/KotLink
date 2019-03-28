package me.devoxin.kotlink

import me.devoxin.kotlink.entities.AudioTrack

object Util {

    fun <T: Any> randomOrNull(list: List<T>): T? {
        return if (list.isEmpty()) {
            null
        } else {
            list.random()
        }
    }

    //public fun toAudioTrack(base64: String): AudioTrack {

    //}

}
