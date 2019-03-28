package me.devoxin.kotlink.entities

import org.json.JSONObject

data class AudioTrack(
    public val track: String,
    public val identifier: String,
    public val isSeekable: Boolean,
    public val author: String,
    public val length: Long,
    public val isStream: Boolean,
    public val position: Long,
    public val title: String,
    public val uri: String
) {

    companion object {

        fun fromJson(json: JSONObject): AudioTrack {
            val info = json.getJSONObject("info")

            val track = json.getString("track")
            val identifier = info.getString("identifier")
            val isSeekable = info.getBoolean("isSeekable")
            val author = info.getString("author")
            val length = info.getLong("length")
            val isStream = info.getBoolean("isStream")
            val position = info.getLong("position")
            val title = info.getString("title")
            val uri = info.getString("uri")

            return AudioTrack(track, identifier, isSeekable, author, length,
                isStream, position, title, uri)
        }

    }
}
