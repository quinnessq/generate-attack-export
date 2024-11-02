package org.mcse.data.generate.models.doa

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "request")
data class Request(
    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    @JoinColumn(name = "entryid", updatable = true)
    @JsonIgnore
    var entry: Entry? = null,

    val authenticated: Boolean = false,
    val requestLength: Int,
    val requestContentLength: Int,
    val requestContentType: String,
    val requestMethod: String,
    val requestUri: String,
    val referrer: String,
    val protocol: String,
    val userAgent: String,
)
