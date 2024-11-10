package org.mcse.data.generate.models.attack

data class AttackingClient(
    val ip: String,
    val port: Int,
    val connecttionId: Long,
    val userAgent: String,
    val protocol: String,
)
