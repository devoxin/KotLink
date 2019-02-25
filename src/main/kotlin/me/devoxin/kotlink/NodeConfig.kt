package me.devoxin.kotlink

public data class NodeConfig(
    public val name: String,
    public val address: String,
    public val wsPort: Int = 2333,
    public val restPort: Int = wsPort,
    public val password: String,
    public val region: String
)
