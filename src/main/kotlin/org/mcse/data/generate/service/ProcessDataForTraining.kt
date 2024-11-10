package org.mcse.data.generate.service

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class ProcessDataForTraining(
    private val generateAttackService: GenerateAttackService,
    private val saveDataForTraining: SaveDataForTraining,
) {


    @PostConstruct
    fun init() {
        //generateAttack()
        saveFiles()
    }

    private fun generateAttack() {
        generateAttackService.generateAttack()
    }

    private fun saveFiles() {
        saveDataForTraining.saveData()
    }

}
