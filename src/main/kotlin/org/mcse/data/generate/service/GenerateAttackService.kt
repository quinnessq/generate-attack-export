package org.mcse.data.generate.service

import org.mcse.data.generate.models.doa.Entry
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GenerateAttackService(
) {

    companion object {
        private val MIN_DATE_TIME = LocalDateTime.parse("2024-09-28T00:00:00")
        private val MAX_DATE_TIME = LocalDateTime.parse("2024-10-24T23:59:59")
        private val CLIENT_RANGE_ATTACK = 50..250
        private val NUM_ATTACK = 3..10
        private val MIN_ATTACK_REQUEST_INTERVAL_SEC = 0.25
        private val MAX_ATTACK_REQUEST_INTERVAL_SEC = 0.75
        
        //Mined data
        private val CALL_VECTORS_ATTACK_1 = linkedSetOf<Entry>()
        private val CALL_VECTORS_ATTACK_2 = linkedSetOf<Entry>()
        private val CALL_VECTORS_ATTACK_3 = linkedSetOf<Entry>()
    }

    fun generateAttack() {

    }

}
