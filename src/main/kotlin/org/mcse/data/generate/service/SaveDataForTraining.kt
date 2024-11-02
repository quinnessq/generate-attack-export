package org.mcse.data.generate.service

import org.springframework.stereotype.Service
import java.io.File

@Service
class SaveDataForTraining {

    companion object {
        private const val RESOURCE_FOLDER = "C:\\Users\\alcui\\Desktop\\MSCE\\Modules\\Afstuderen\\trainingdata"
        private val fileList: MutableSet<File> = mutableSetOf()

    }

}
