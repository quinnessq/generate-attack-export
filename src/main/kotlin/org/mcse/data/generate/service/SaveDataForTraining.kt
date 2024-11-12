package org.mcse.data.generate.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

@Service
class SaveDataForTraining(
    private val entryDomainService: EntryDomainService,
) {

    companion object {
        private const val RESOURCE_FOLDER = "C:\\Users\\alcui\\Desktop\\MSCE\\Modules\\Afstuderen\\trainingdata\\"
        private const val TRAINING_DATA = "training.csv"
        private const val VALIDATION_DATA = "validation.csv"
        private const val MAX_PAGE_SIZE = 1000
        private const val START_OFFSET = 0
    }

    fun saveData() {
        saveDataFor(false)
        saveDataFor(true)
    }

    private fun saveDataFor(validation: Boolean) {
        val filePath = if(validation) {
            Paths.get( RESOURCE_FOLDER+VALIDATION_DATA)
        } else {
            Paths.get(RESOURCE_FOLDER+TRAINING_DATA)
        }
        val totalEntries = entryDomainService.getCountFor(validation)
        val mapxPages = totalEntries.div(MAX_PAGE_SIZE).toInt()
        val exportData = StringBuilder()

        if(START_OFFSET == 0 ) exportData.appendLine("date,time,malicious,remote_ip,remote_port,connection_id,connection_time,upstream_response_time,upstream_response_length,upstream_status,upstream_connection_time,response_body_size,response_total_size,response_status,response_time,requestLength,request_content_length,request_content_type,request_method,request_uri,referrer,protocol,user_agent")
        (START_OFFSET..mapxPages).forEach { pageIndex ->
            val entries = entryDomainService.getEntitiesFor(validation, PageRequest.of(pageIndex, MAX_PAGE_SIZE))
            entries.forEach { entry ->
                exportData.appendLine(entry.printForExport())
            }
            Files.write(filePath, exportData.toString().toByteArray(), StandardOpenOption.APPEND)
            exportData.clear()
            println("Processed data for validation $validation and page $pageIndex")
        }
        println("Saved data for validation $validation")
    }

}
