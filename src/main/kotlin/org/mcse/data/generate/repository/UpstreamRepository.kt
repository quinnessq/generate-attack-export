package org.mcse.data.generate.repository

import org.mcse.data.generate.models.doa.Upstream
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UpstreamRepository : JpaRepository<Upstream, Long>
