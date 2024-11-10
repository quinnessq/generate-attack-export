package org.mcse.data.generate.models.attack

data class RequestContent(
    val uri: String,
    val length: Int,
    val contentLength: Int = 0,
)
