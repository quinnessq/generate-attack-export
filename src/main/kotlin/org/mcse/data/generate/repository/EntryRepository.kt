package org.mcse.data.generate.repository

import org.mcse.data.generate.models.doa.Entry
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EntryRepository : JpaRepository<Entry, Long> {

    @Query(value = "SELECT count(*) FROM Entry WHERE validation = :validation")
    fun countFor(validation: Boolean): Long

    fun findAllByValidationIsFalse(pageable: Pageable): Page<Entry>

    fun findAllByValidationIsTrue(pageable: Pageable): Page<Entry>
}
