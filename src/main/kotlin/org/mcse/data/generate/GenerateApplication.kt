package org.mcse.data.generate

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary


@SpringBootApplication
class GenerateApplication {

	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			runApplication<GenerateApplication>(*args)
		}
	}

	@Bean
	@Primary
	fun objectMapper(): ObjectMapper = jacksonObjectMapper()
		.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
		.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		.setSerializationInclusion(JsonInclude.Include.NON_NULL)
		.registerModule(JavaTimeModule())
}
