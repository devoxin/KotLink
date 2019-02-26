package me.devoxin.kotlink.entities

import org.json.JSONObject

data class AudioTrack(private val obj: JSONObject) {
    private val info = obj.getJSONObject("info")

    public val track = obj.getString("track")
    public val identifier = info.getString("identifier")
    public val isSeekable = info.getBoolean("isSeekable")
    public val author = info.getString("author")
    public val length = info.getLong("length")
    public val isStream = info.getBoolean("isStream")
    public val position = info.getLong("position")
    public val title = info.getString("title")
    public val uri = info.getString("uri")
}
