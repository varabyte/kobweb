package com.varabyte.kobweb.gradle.core.utils

import org.gradle.api.logging.Logger

/** Interface that abstracts out reporting error messages. */
interface Reporter {
    fun report(message: String)
}

class LoggingReporter(private val logger: Logger) : Reporter {
    override fun report(message: String) {
        logger.error("e: $message") // Prepend with "e:" so Kobweb CLI highlights it as an error
    }
}