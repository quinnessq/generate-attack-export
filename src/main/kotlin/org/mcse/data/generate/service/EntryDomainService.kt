package org.mcse.data.generate.service

import org.hibernate.StaleStateException
import org.mcse.data.generate.models.doa.Entry
import org.mcse.data.generate.repository.ConnectionRepository
import org.mcse.data.generate.repository.EntryRepository
import org.mcse.data.generate.repository.RequestRepository
import org.mcse.data.generate.repository.ResponseRepository
import org.mcse.data.generate.repository.UpstreamRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class EntryDomainService(
    private val entryRepository: EntryRepository,
    private val connectionRepository: ConnectionRepository,
    private val responseRepository: ResponseRepository,
    private val requestRepository: RequestRepository,
    private val upstreamRepository: UpstreamRepository,
) {

    @Transactional(rollbackFor = [StaleStateException::class], propagation = Propagation.REQUIRED, readOnly = false)
    fun saveEntity(entry: Entry) {
        val entrySaved = entryRepository.saveAndFlush(entry)
        if(entry.connection != null) connectionRepository.saveAndFlush(entry.connection.copy(entry = entrySaved))
        if(entry.response != null) responseRepository.saveAndFlush(entry.response.copy(entry = entrySaved))
        if(entry.request != null) requestRepository.saveAndFlush(entry.request.copy(entry = entrySaved))
        if(entry.upstream != null) upstreamRepository.saveAndFlush(entry.upstream.copy(entry = entrySaved))
    }

    @Transactional(rollbackFor = [StaleStateException::class], propagation = Propagation.REQUIRED, readOnly = true)
    fun getCountFor(validation: Boolean): Long {
        return entryRepository.countFor(validation)
    }

    @Transactional(rollbackFor = [StaleStateException::class], propagation = Propagation.REQUIRED, readOnly = true)
    fun getEntitiesFor(validation: Boolean, pageAble: Pageable): Page<Entry> {
        return if(validation) {
            entryRepository.findAllByValidationIsTrue(pageAble)
        } else {
            entryRepository.findAllByValidationIsFalse(pageAble)
        }
    }

}
