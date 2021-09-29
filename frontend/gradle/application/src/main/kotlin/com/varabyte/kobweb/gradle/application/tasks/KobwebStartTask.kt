@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.KobwebFolder
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerState
import com.varabyte.kobweb.server.api.ServerStateFile
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

private fun ServerState.toDisplayText(): String {
    return "http://localhost:$port (PID = $pid)"
}

/**
 * Start a Kobweb web server.
 *
 * Note that this task is NOT blocking. It will start a server in the background and then return success immediately.
 *
 * You should execute the [KobwebStopTask] to stop a server started by this task.
 */
abstract class KobwebStartTask @Inject constructor(private val env: ServerEnvironment) :
    KobwebTask("Start a Kobweb server") {

    @TaskAction
    fun execute() {
        val kobwebFolder = KobwebFolder.inWorkingDirectory()
            ?: throw GradleException("This project is missing a Kobweb root folder")

        val stateFile = ServerStateFile(kobwebFolder)
        stateFile.content?.let { serverState ->
            if (serverState.isRunning()) {
                println("A Kobweb server is already running at ${serverState.toDisplayText()}")
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