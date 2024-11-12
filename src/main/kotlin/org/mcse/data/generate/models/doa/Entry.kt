package org.mcse.data.generate.models.doa

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Entity
@Table(name = "entry")
data class Entry(
    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val date: LocalDateTime,

    val time: BigDecimal,

    val malicious: Boolean = false,

    val validation: Boolean = false,

    @OneToOne(targetEntity = Connection::class, mappedBy = "entry", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    val connection: Connection? = null,

    @OneToOne(targetEntity = Upstream::class, mappedBy = "entry", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    val upstream: Upstream? = null,

    @OneToOne(targetEntity = Response::class, mappedBy = "entry", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    val response: Response? = null,

    @OneToOne(targetEntity = Request::class, mappedBy = "entry", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    val request: Request? = null,

    ) {

    fun printForExport(): String {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateString = dateTimeFormatter.format(date)
        return """
            $dateString,$time,$malicious,${connection?.remoteIp},${connection?.remotePort},${connection?.connectionId},${connection?.connectionTime},${upstream?.upstreamResponseTime},${upstream?.upstreamResponseLength},${upstream?.upstreamStatus},${upstream?.upstreamConnectionTime},${response?.responseBodySize},${response?.responseTotalSize},${response?.responseStatus},${response?.responseTime}, ${request?.requestLength},${request?.requestContentLength},"${request?.requestContentType}","${request?.requestMethod}","${request?.requestUri}","${request?.referrer}","${request?.protocol}","${request?.userAgent}"
        """.trimIndent()
    }
}
