@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.io.consumeAsync
import com.varabyte.kobweb.gradle.application.util.toDisplayText
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerStateFile
import com.varabyte.kobweb.server.api.SiteLayout
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
    private val siteLayout: SiteLayout,
    private val reuseServer: Boolean
) : KobwebTask("Start a Kobweb server") {

    @TaskAction
    fun execute() {
        val stateFile = ServerStateFile(kobwebApplication.kobwebFolder)
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

        val processParams = arrayOf(
            "$javaHome/bin/java",
            env.toSystemPropertyParam(),
            siteLayout.toSystemPropertyParam(),
            // See: https://ktor.io/docs/development-mode.html#system-property
            "-Dio.ktor.development=${env == ServerEnvironment.DEV}",
            "-jar",
            serverJar.absolutePath,
        )

        println(
            """
            Starting server by running:
                ${processParams.joinToString(" ")}
            """.trimIndent()
        )
        // Flush above println. Otherwise, it can end up mixed-up in exception reporting below.
        System.out.flush()

        val process = Runtime.getRuntime().exec(
            processParams,
            emptyArray(), // empty env
            kobwebApplication.path.toFile()
        )

        val errorMessage = StringBuilder()
        process.errorStream.consumeAsync { line -> errorMessage.appendLine(line) }

        while (stateFile.content == null && process.isAlive) {
            Thread.sleep(300)
        }
        stateFile.content?.let { serverState ->
            println("A Kobweb server is now running at ${serverState.toDisplayText()}")
            println()
            println("Run `gradlew kobwebStop` when you're ready to shut it down.")
        } ?: run {
            throw GradleException(buildString {
                append("Unable to start the Kobweb server.")
                if (errorMessage.isNotEmpty()) {
                    append("\n\nError: $errorMessage")
                }
            })
        }
    }
}