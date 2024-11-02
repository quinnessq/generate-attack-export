package org.mcse.data.generate.models.doa

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

@Entity
@Table(name = "entry")
data class Entry(
    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val date: LocalDateTime,

    val time: BigDecimal,

    val malicious: Boolean = false,

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
)
