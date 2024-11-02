package org.mcse.data.generate.repository

import org.mcse.data.generate.models.doa.Request
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RequestRepository : JpaRepository<Request, Long>