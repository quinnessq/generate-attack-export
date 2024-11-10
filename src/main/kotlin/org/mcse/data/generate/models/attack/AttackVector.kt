package org.mcse.data.generate.models.attack

data class AttackVector(
    val uri: String,
    val setupPath: Boolean,
    val formatUri: Boolean,
    val method: String,
    val contentLength: IntRange? = null,
)
