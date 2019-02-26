package me.devoxin.kotlink.entities

class AudioResult(
    val loadResult: String,
    val name: String?,
    val selectedIndex: Int?,
    val tracks: List<AudioTrack>
) {

    companion object {
        fun empty(result: String): AudioResult = AudioResult(result, null, null, emptyList())
    }

}

/**
 * Load Results:
 *  TRACK_LOADED - The result is a track. (Direct URL).
 *  PLAYLIST_LOADED - The result is a playlist. (SoundCloud, YouTube).
 *  SEARCH_RESULT - The result is a search result. (SoundCloud, YouTube).
 *  NO_MATCHES - No results found relating to the given query.
 *  LOAD_FAILED - Track found, but couldn't be loaded.
 *  UNKNOWN - An issue was encountered while trying to make the request.
 */
