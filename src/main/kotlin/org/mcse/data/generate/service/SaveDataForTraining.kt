package org.mcse.data.generate.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.io.File

@Service
class SaveDataForTraining(
    private val entryDomainService: EntryDomainService,
) {

    companion object {
        private const val RESOURCE_FOLDER = "C:\\Users\\alcui\\Desktop\\MSCE\\Modules\\Afstuderen\\trainingdata\\"
        private const val TRAINING_DATA = "training.csv"
        private const val VALIDATION_DATA = "validation.csv"
        private const val MAX_PAGE_SIZE = 1000
    }

    fun saveData() {
        saveDataFor(false)
        saveDataFor(true)
    }

    private fun saveDataFor(validation: Boolean) {
        val totalEntries = entryDomainService.getCountFor(validation)
        val mapxPages = totalEntries.div(MAX_PAGE_SIZE).toInt()
        val exportData = StringBuilder()
        exportData.appendLine("date,time,malicious,remote_ip,remote_port,connection_id,connection_time,upstream_response_time,upstream_response_length,upstream_status,upstream_connection_time,response_body_size,response_total_size,response_status,response_time,requestLength,request_content_length,request_content_type,request_method,request_uri,referrer,protocol,user_agent")
        (0..mapxPages).forEach { pageIndex ->
            val entries = entryDomainService.getEntitiesFor(validation, PageRequest.of(pageIndex, MAX_PAGE_SIZE))
            entries.forEach { entry ->
                exportData.appendLine(entry.printForExport())
            }
            println("Processed data for validation $validation and page $pageIndex")
        }
        val file = if(validation) {
            File(RESOURCE_FOLDER+VALIDATION_DATA)
        } else {
            File(RESOURCE_FOLDER+TRAINING_DATA)
        }
        file.writeText(exportData.toString())
        println("Saved data for validation $validation")
    }

}
