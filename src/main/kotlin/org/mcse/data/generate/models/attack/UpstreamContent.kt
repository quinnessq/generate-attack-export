package org.mcse.data.generate.models.attack

import java.math.BigDecimal

data class UpstreamContent(
    val upstreamResponseTime: BigDecimal? = BigDecimal.ZERO,
    val upstreamResponseLength: Int,
    val upstreamStatus: Int,
)
