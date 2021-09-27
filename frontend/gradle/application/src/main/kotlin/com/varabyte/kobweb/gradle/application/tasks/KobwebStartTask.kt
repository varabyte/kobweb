@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.KobwebFolder
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
 */
abstract class KobwebStartTask @Inject constructor(private val env: ServerEnvironment) :
    KobwebTask("Start a Kobweb server") {

    @TaskAction
    fun execute() {
        val kobwebFolder = KobwebFolder.inWorkingDirectory()
            ?: throw GradleException("This project is missing a Kobweb root folder")

        val javaHome = System.getProperty("java.home")!!

        val serverJar = KobwebStartTask::class.java.getResourceAsStream("/server.jar")!!.let { stream ->
            File.createTempFile("server", ".jar").apply {
                appendBytes(stream.readAllBytes())
                deleteOnExit()
            }
        }

        val process = Runtime.getRuntime()
            .exec(arrayOf("$javaHome/bin/java", env.toSystemPropertyParam(), "-jar", serverJar.absolutePath))

        val stateFile = ServerStateFile(kobwebFolder)
        while (stateFile.content == null && process.isAlive) {
            Thread.sleep(300)
        }
        stateFile.content?.let { serverState ->
            println("\t${serverState.port} @ ${serverState.pid}")
        } ?: throw GradleException("Unable to start the Kobweb server")
    }
}