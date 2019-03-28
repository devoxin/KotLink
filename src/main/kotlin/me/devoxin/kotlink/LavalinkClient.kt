package me.devoxin.kotlink

import me.devoxin.kotlink.entities.AudioPlayer
import me.devoxin.kotlink.entities.AudioResult
import me.devoxin.kotlink.entities.AudioTrack
import me.devoxin.kotlink.entities.PlaylistInfo
import okhttp3.*
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.concurrent.CompletableFuture

class LavalinkClient(
    public val userId: String,
    public val shardCount: Int,
    customRegions: HashMap<String, Array<String>>? = null
) {

    private val log = LoggerFactory.getLogger(LavalinkClient::class.java)
    private val httpClient = OkHttpClient()
    private val defaultRegions = hashMapOf(
        "asia" to arrayOf("hongkong", "singapore", "sydney", "japan", "southafrica"),
        "eu" to arrayOf("eu", "amsterdam", "frankfurt", "russia", "london"),
        "us" to arrayOf("us", "brazil")
    )

    public val regions = customRegions ?: defaultRegions
    public val nodes = mutableListOf<Node>()
    public val players = hashMapOf<Long, AudioPlayer>()

    public val availableNodes: List<Node>
        get() = nodes.filter { it.available }

    fun addNode(config: NodeConfig) {
        if (!regions.containsKey(config.region)) {
            throw NodeException("${config.region} is not a valid region!")
        }

        val headers = mapOf(
            "Authorization" to config.password,
            "Num-Shards" to shardCount.toString(),
            "User-Id" to userId
        )

        val node = Node(this, config, headers)
        node.connect()
        nodes.add(node)
    }

    /**
     * Gets a AudioPlayer used for controlling music playback.
     * @param guildId The guildId the player should belong to.
     * @return AudioPlayer
     */
    fun getPlayer(guildId: Long) = players[guildId]

    fun createPlayer(guildId: Long): AudioPlayer {
        return players.computeIfAbsent(guildId) {
            AudioPlayer(this, nodes.first(), guildId)
        }
    }

    /**
     * Sends a search request to the given node if provided, otherwise a random node.
     * @param query The query to search for.
     * @param node The node to perform the search on. Can be omitted to use a random node.
     * @returns AudioResult
     */
    fun getTracks(query: String, node: Node? = null): CompletableFuture<AudioResult> {
        val future = CompletableFuture<AudioResult>()

        if (node != null && !node.available) {
            future.completeExceptionally(Error("Provided node is not available!"))
            return future
        }

        val targetNode = node ?: Util.randomOrNull(availableNodes)

        if (targetNode == null) {
            future.completeExceptionally(Error("get some better nodes cuck"))
            return future
        }

        val encodedQuery = URLEncoder.encode(query, Charset.defaultCharset())

        val req = Request.Builder()
            .url("${targetNode.restUrl}/loadtracks?identifier=$encodedQuery")
            .header("Authorization", targetNode.config.password)
            .build()

        log.debug("[<-] Requesting tracks from node ${targetNode.config.name}")

        httpClient.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                log.error("Failed to retrieve tracks from node ${targetNode.config.name}", e)
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                log.debug("[->] Response from node ${targetNode.config.name} with status code ${response.code()}")
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    future.completeExceptionally(
                        Error("Invalid loadtracks response: ${response.code()} - ${response.message()}")
                    )
                    return
                }

                val json = JSONObject(body.string())
                val loadResult = json.getString("loadType")
                val playlistInfo = PlaylistInfo(json.getJSONObject("playlistInfo"))
                val trackList = mutableListOf<AudioTrack>()

                for (track in json.getJSONArray("tracks")) {
                    trackList.add(AudioTrack.fromJson(track as JSONObject))
                }

                future.complete(AudioResult(loadResult, playlistInfo, trackList))
            }
        })

        return future
    }

}