package me.devoxin.kotlink

import okhttp3.*
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.concurrent.CompletableFuture

class Client(
    public val userId: String,
    public val shardCount: Int,
    customRegions: HashMap<String, Array<String>>? = null
) {

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
    fun getTracks(query: String, node: Node? = null): CompletableFuture<List<AudioTrack>> {
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

        val future = CompletableFuture<List<AudioTrack>>()

        val encodedQuery = URLEncoder.encode(query, Charset.defaultCharset())
        val url = "${targetNode.restUrl}/loadTracks?identifier=$encodedQuery"

        val req = Request.Builder()
            .url(url)
            .header("Authorization", targetNode.config.password)
            .build()

        httpClient.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {

            }
        })

        return future
    }

}