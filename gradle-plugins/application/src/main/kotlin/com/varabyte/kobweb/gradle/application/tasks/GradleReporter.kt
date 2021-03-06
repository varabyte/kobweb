package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.project.Reporter
import org.gradle.api.logging.Logger

class GradleReporter(private val logger: Logger) : Reporter {
    override fun report(message: String) {
        logger.error(message)
    }
}