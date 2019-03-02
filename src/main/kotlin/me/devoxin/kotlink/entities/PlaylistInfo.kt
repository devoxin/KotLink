package me.devoxin.kotlink.entities

import org.json.JSONObject

data class PlaylistInfo(
    private val d: JSONObject,
    public val name: String? = d.optString("name"),
    public val selectedTrack: Int? = d.optInt("selectedTrack")
)
