package me.devoxin.kotlink

import java.net.URI

class Client(
    public val userId: String,
    public val shardCount: Int,
    customRegions: HashMap<String, Array<String>>? = null
) {

    private val defaultRegions = hashMapOf(
        "asia" to arrayOf("hongkong", "singapore", "sydney", "japan", "southafrica"),
        "eu" to arrayOf("eu", "amsterdam", "frankfurt", "russia", "london"),
        "us" to arrayOf("us", "brazil")
    )

    public val regions = customRegions ?: defaultRegions
    public val nodes = mutableListOf<Node>()

    fun addNode(name: String, serverUri: URI, password: String, region: String) {
        if (!regions.containsKey(region)) {
            throw NodeException("$region is not a valid region!")
        }

        val headers = hashMapOf(
            "Authorization" to password,
            "Num-Shards" to shardCount.toString(),
            "User-Id" to userId
        )

        val node = Node(this, name, region, serverUri, headers)
        node.connect()
        nodes.add(node)
    }

}