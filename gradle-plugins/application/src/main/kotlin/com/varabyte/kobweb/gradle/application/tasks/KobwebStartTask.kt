@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerStateFile
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

/**
 * Start a Kobweb web server.
 *
 * Note that this task is NOT blocking. It will start a server in the background and then return success immediately.
 *
 * You should execute the [KobwebStopTask] to stop a server started by this task.
 *
 * @param reuseServer If a server is already running, re-use it if possible.
 */
abstract class KobwebStartTask @Inject constructor(
    private val env: ServerEnvironment,
    private val reuseServer: Boolean
) : KobwebTask("Start a Kobweb server") {

    @TaskAction
    fun execute() {
        val stateFile = ServerStateFile(kobwebProject.kobwebFolder)
        stateFile.content?.let { serverState ->
            val alreadyRunningMessage = "A Kobweb server is already running at ${serverState.toDisplayText()}"
            if (serverState.isRunning()) {
                if (!reuseServer) {
                    throw GradleException("$alreadyRunningMessage and cannot be reused for this task.")
                } else if (serverState.env != env) {
                    throw GradleException(
                        alreadyRunningMessage
                            + " but can't be reused because it is using a different environment (want=$env, current=${serverState.env})"
                    )
                } else {
                    println(alreadyRunningMessage)
                }
                return
            }
        }

        val javaHome = System.getProperty("java.home")!!

        val serverJar = KobwebStartTask::class.java.getResourceAsStream("/server.jar")!!.let { stream ->
            File.createTempFile("server", ".jar").apply {
                appendBytes(stream.readAllBytes())
                deleteOnExit()
            }
        }

        val process = Runtime.getRuntime()
            .exec(arrayOf("$javaHome/bin/java", env.toSystemPropertyParam(), "-jar", serverJar.absolutePath))

        while (stateFile.content == null && process.isAlive) {
            Thread.sleep(300)
        }
        stateFile.content?.let { serverState ->
            println("A Kobweb server is now running at ${serverState.toDisplayText()}")
        } ?: throw GradleException("Unable to start the Kobweb server")
    }
}