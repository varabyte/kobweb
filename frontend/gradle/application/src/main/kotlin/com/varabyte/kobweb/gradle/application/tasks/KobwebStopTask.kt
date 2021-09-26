@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * Stop a Kobweb web server started by the [KobwebStartTask].
 *
 * This task will block until it can confirm the server is no longer running.
 */
abstract class KobwebStopTask @Inject constructor(private val config: KobwebConfig) :
    KobwebTask("Stop a Kobweb server if one is running") {

    @TaskAction
    fun execute() {
        // TODO: Implement me
        println("Hello from KobwebStopTask")
    }
}