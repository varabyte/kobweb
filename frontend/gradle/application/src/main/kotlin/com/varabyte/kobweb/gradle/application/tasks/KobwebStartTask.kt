@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

enum class ServerEnvironment {
    DEV,
    PROD
}

/**
 * Start a Kobweb web server.
 *
 * Note that this task is NOT blocking. It will start a server in the background and then return success immediately.
 *
 * You should execute the [KobwebStopTask] to stop a server started by this task.
 */
abstract class KobwebStartTask @Inject constructor(
    private val config: KobwebConfig,
    private val env: ServerEnvironment)
    : KobwebTask("Start a Kobweb server") {

    @TaskAction
    fun execute() {
        // TODO: Implement me
        println("Hello from KobwebStartTask. Env: $env")
    }
}