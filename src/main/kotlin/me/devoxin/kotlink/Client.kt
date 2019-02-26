package me.devoxin.kotlink

import me.devoxin.kotlink.entities.AudioResult
import me.devoxin.kotlink.entities.AudioTrack
import okhttp3.*
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.concurrent.CompletableFuture

class Client(
    public val userId: String,
    public val shardCount: Int,
    customRegions: HashMap<String, Array<String>>? = null
) {

    private val LOG = LoggerFactory.getLogger(Client::class.java)
    private val httpClient = OkHttpClient()
    private val defaultRegions = hashMapOf(
        "asia" to arrayOf("hongkong", "singapore", "sydney", "japan", "southafrica"),
        "eu" to arrayOf("eu", "amsterdam", "frankfurt", "russia", "london"),
        "us" to arrayOf("us", "brazil")
    )

    public val regions = customRegions ?: defaultRegions
    public val nodes = mutableListOf<Node>()

    fun addNode(config: NodeConfig) {
        if (!regions.containsKey(config.region)) {
            throw NodeException("${config.region} is not a valid region!")
        }

        val headers = hashMapOf(
            "Authorization" to config.password,
            "Num-Shards" to shardCount.toString(),
            "User-Id" to userId
        )

        val node = Node(this, config, headers)
        node.connect()
        nodes.add(node)
    }


    /**
     * Sends a search request to the given node if provided, otherwise a random node.
     * @param query The query to search for.
     * @param node The node to perform the search on. Can be omitted to use a random node.
     * @returns List<AudioTrack>
     */
    fun getTracks(query: String, node: Node? = null): CompletableFuture<AudioResult> {
        if (node != null && !node.available) {
            throw Error("Provided node is not available!")
        }

        val targetNode = if (node != null) {
            node
        } else {
            val availableNodes = nodes.filter { it.available }

            if (availableNodes.isEmpty()) {
                throw Error("No available nodes!")
            }

            availableNodes.random()
        }

        val future = CompletableFuture<AudioResult>()

        val encodedQuery = URLEncoder.encode(query, Charset.defaultCharset())
        val url = "${targetNode.restUrl}/loadTracks?identifier=$encodedQuery"

        val req = Request.Builder()
            .url(url)
            .header("Authorization", targetNode.config.password)
            .build()

        LOG.debug("[<-] Requesting tracks from node ${targetNode.config.name}")

        httpClient.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                LOG.error("Failed to retrieve tracks from node ${targetNode.config.name}", e)
                future.complete(AudioResult.empty("UNKNOWN"))
            }

            override fun onResponse(call: Call, response: Response) {
                LOG.debug("[->] Response from node ${targetNode.config.name} with status code ${response.code()}")
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    future.complete(AudioResult.empty("UNKNOWN"))
                    return
                }

                val json = JSONObject(body.string())
                val loadResult = json.getString("loadType")
                val trackList = mutableListOf<AudioTrack>()

                json.getJSONArray("tracks").forEach {
                    trackList.add(AudioTrack(it as JSONObject))
                }

                when (loadResult) {
                    "TRACK_LOADED" -> future.complete(
                        AudioResult(loadResult, null, null, trackList)
                    )
                    "PLAYLIST_LOADED" -> {
                        val playlistInfo = json.getJSONObject("playlistInfo")
                        val name = playlistInfo.getString("name")
                        val selectedTrack = playlistInfo.getInt("selectedTrack")
                        future.complete(
                            AudioResult(loadResult, name, selectedTrack, trackList)
                        )
                    }
                    "SEARCH_RESULT" -> future.complete(
                        AudioResult(loadResult, null, null, trackList)
                    )
                    "NO_MATCHES" -> future.complete(AudioResult.empty(loadResult))
                    "LOAD_FAILED" -> future.complete(AudioResult.empty(loadResult))
                }
            }
        })

        return future
    }

}