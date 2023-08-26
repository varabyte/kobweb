package com.varabyte.kobweb.gradle.core.util

import org.gradle.api.logging.Logger

/** Interface that abstracts out reporting error messages. */
interface Reporter {
    fun error(message: String)
    fun warn(message: String)
}

class LoggingReporter(private val logger: Logger) : Reporter {
    override fun error(message: String) {
        logger.error("e: $message") // Prepend with "e:" so Kobweb CLI highlights it as an error
    }

    override fun warn(message: String) {
        logger.warn("w: $message") // Prepend with "w:" so Kobweb CLI highlights it as a warning
    }
}