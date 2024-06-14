package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.io.consumeAsync
import com.varabyte.kobweb.common.path.invariantSeparatorsPath
import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.util.toDisplayText
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerStateFile
import com.varabyte.kobweb.server.api.SiteLayout
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
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
    private val remoteDebuggingBlock: AppBlock.ServerBlock.RemoteDebuggingBlock,
    private val env: ServerEnvironment,
    private val siteLayout: SiteLayout,
    private val reuseServer: Boolean,
) : KobwebTask("Start a Kobweb server") {

    @get:InputFile
    abstract val serverJar: RegularFileProperty

    // This is not directly used by the task, but is used by the server which this task starts, so we configure the
    // directory as an input so that gradle handles task dependencies correctly.
    // Note: we don't use @InputDirectory as it doesn't support optional directories (https://github.com/gradle/gradle/issues/2016)
    @get:InputFiles
    abstract val serverPluginsDir: DirectoryProperty

    @TaskAction
    fun execute() {
        val stateFile = ServerStateFile(kobwebApplication.kobwebFolder)
        stateFile.content?.let { serverState ->
            if (serverState.isRunning()) {
                val alreadyRunningMessage = "A Kobweb server is already running at ${serverState.toDisplayText()}"
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

        val javaHome = System.getenv("KOBWEB_JAVA_HOME") ?: System.getProperty("java.home")!!
        val remoteDebuggingEnabled = (env == ServerEnvironment.DEV && remoteDebuggingBlock.enabled.get())
        val processParams = buildList<String> {
            add("${javaHome.invariantSeparatorsPath}/bin/java")
            add(env.toSystemPropertyParam())
            add(siteLayout.toSystemPropertyParam())
            // See: https://ktor.io/docs/development-mode.html#system-property)
            add("-Dio.ktor.development=${env == ServerEnvironment.DEV}")
            if (env == ServerEnvironment.DEV && remoteDebuggingEnabled) {
                add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${remoteDebuggingBlock.port.get()}")
            }
            add("-jar")
            add(serverJar.get().asFile.absolutePath)
        }.toTypedArray()

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
            // Note: We intentionally set envp null here, to inherit our environment. One of the
            // things that gets inherited is the tmp file location, which seems to be particularly
            // important on Windows, as it will otherwise try to create temp files in folders that
            // we don't have permissions to write to... (See #208)
            null,
            kobwebApplication.path.toFile()
        )

        process.inputStream.consumeAsync {
            // We're not observing server output now, but maybe we will in the future.
            // You'd think therefore we should delete this handler, but it actually seems
            // to help avoid the server stalling on startup in Windows.
            // So until we understand the root problem, we'll just leave this in for now.

            // Potentially related discussions:
            // - https://github.com/gradle/gradle/issues/16716
            //   Running a child process from Gradle on Windows and trying to read the stdin
            //   via inheritIO() will cause the waitFor() to hang endlessly. The reason is
            //   most probably that the inheritIO() is not properly piped out from Gradle's
            //   process, causing the stdout buffer to overflow and the child process to block.
            //   The issue is only reproducible on Windows 10, most probably because Windows 10
            //   stdout buffer is rather small.
            // - https://docs.oracle.com/javase/7/docs/api/java/lang/Process.html
            //   Because some native platforms only provide limited buffer size for standard
            //   input and output streams, failure to promptly write the input stream or read
            //   the output stream of the subprocess may cause the subprocess to block, or
            //   even deadlock.
        }

        val errorMessage = StringBuilder()
        process.errorStream.consumeAsync { line -> errorMessage.appendLine(line) }

        // Note: We protect against old state files left around from previous runs by checking the PID explicitly.
        // If the PIDs don't match, the file will get overwritten shortly.
        while (process.isAlive && (stateFile.content.let { content ->
                content == null || content.pid != process.pid()
            })) {
            Thread.sleep(300)
        }
        stateFile.content?.let { serverState ->
            println("A Kobweb server is now running at ${serverState.toDisplayText()}")
            if (remoteDebuggingEnabled) {
                println("Remote debugging is enabled. You may attach a debugger to port ${remoteDebuggingBlock.port.get()}.")
            }
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
