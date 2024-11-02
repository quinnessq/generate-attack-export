package org.mcse.data.generate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class GenerateApplication {

	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			runApplication<GenerateApplication>(*args)
		}
	}
}
