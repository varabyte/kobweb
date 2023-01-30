package com.varabyte.kobweb.cli.common

import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.SiteLayout
import com.varabyte.kotter.foundation.collections.liveListOf
import com.varabyte.kotter.foundation.input.Key
import com.varabyte.kotter.foundation.input.Keys
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.text.red
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotter.foundation.text.yellow
import com.varabyte.kotter.runtime.RunScope
import com.varabyte.kotter.runtime.Session
import com.varabyte.kotter.runtime.concurrent.createKey
import com.varabyte.kotter.runtime.render.RenderScope
import org.gradle.tooling.CancellationTokenSource
import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.ResultHandler
import org.gradle.tooling.internal.consumer.DefaultGradleConnector
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File
import java.io.OutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.text.StringBuilder

class KobwebGradle(private val env: ServerEnvironment, projectDir: File = File(".")): Closeable {
    private val gradleConnector = GradleConnector.newConnector().forProjectDirectory(projectDir).also {
        // The Gradle daemon spawned by the tooling API seems to stick around and not always get removed by
        // ./gradlew --stop for some reason? Adding a timeout seems to be the way that some projects deal with
        // leaked daemons go away when working with the Gradle Tooling API
        // Note we do a safe "as?" check here, for future safety, but it seems that the `DefaultGradleConnector`
        // interface has been used since at least 2014:
        // https://discuss.gradle.org/t/setting-org-gradle-daemon-idletimeout-through-tooling-api/5875
        (it as? DefaultGradleConnector)?.daemonMaxIdleTime(1, TimeUnit.MINUTES)
    }
    private val projectConnection: ProjectConnection = gradleConnector.connect()

    override fun close() {
        projectConnection.close()
        gradleConnector.disconnect()
    }

    class Handle internal constructor(private val cancellationSource: CancellationTokenSource) {
        var lineHandler: (line: String, isError: Boolean) -> Unit = { line, isError ->
            if (isError) {
                System.err.println(line)
            } else {
                println(line)
            }
        }

        var onCompleted: (failure: Exception?) -> Unit = { }

        internal inner class HandleOutputStream(private val isError: Boolean) : OutputStream() {
            private val delegateStream = ByteArrayOutputStream()
            override fun write(b: Int) {
                if (b == 10) {
                    lineHandler.invoke(delegateStream.toString(), isError)
                    delegateStream.reset()
                } else if (b != 13) { // Skip newline bytes on Windows
                    delegateStream.write(b)
                }
            }
        }

        internal val latch = CountDownLatch(1)

        fun cancel() {
            cancellationSource.cancel()
            latch.countDown()
        }

        fun waitFor() {
            latch.await()
        }
    }

    fun gradlew(task: String, vararg args: String): Handle {
        val finalArgs = args.toMutableList()
        finalArgs.add("--stacktrace")

        val cancelToken = GradleConnector.newCancellationTokenSource()
        val handle = Handle(cancelToken)
        projectConnection.newBuild()
            .setStandardOutput(handle.HandleOutputStream(isError = false))
            .setStandardError(handle.HandleOutputStream(isError = true))
            .forTasks(task)
            .withArguments(*finalArgs.toTypedArray())
            .withCancellationToken(cancelToken.token())
            .run(object : ResultHandler<Void> {
                private fun handleFinished() {
                    handle.latch.countDown()
                }

                override fun onComplete(result: Void?) {
                    handle.onCompleted.invoke(null)
                    handleFinished()
                }

                override fun onFailure(failure: GradleConnectionException) {
                    handle.onCompleted.invoke(failure)
                    handleFinished()
                }
            })

        return handle
    }

    fun startServer(enableLiveReloading: Boolean, siteLayout: SiteLayout): Handle {
        val args = mutableListOf("-PkobwebEnv=$env", "-PkobwebRunLayout=$siteLayout")
        if (enableLiveReloading) {
            args.add("-t")
        }
        return gradlew("kobwebStart", *args.toTypedArray())
    }

    fun stopServer(): Handle {
        return gradlew("kobwebStop")
    }

    fun export(siteLayout: SiteLayout): Handle {
        // Even if we are exporting a non-Kobweb layout, we still want to start up a dev server using a Kobweb layout so
        // it looks for the source files in the right place.
        return gradlew("kobwebExport", "-PkobwebReuseServer=false", "-PkobwebEnv=DEV", "-PkobwebRunLayout=KOBWEB", "-PkobwebBuildTarget=RELEASE", "-PkobwebExportLayout=$siteLayout")
    }
}

private const val GRADLE_ERROR_PREFIX = "e: "
private const val GRADLE_WARNING_PREFIX = "w: "
private const val GRADLE_WHAT_WENT_WRONG = "* What went wrong:"
private const val GRADLE_TRY_PREFIX = "* Try:"
private const val GRADLE_TASK_PREFIX = "> Task :"

sealed interface GradleAlert {
    class Warning(val line: String) : GradleAlert
    class Error(val line: String) : GradleAlert
    class Task(val task: String) : GradleAlert
    object BuildRestarted : GradleAlert
}

private val WhatWentWrongKey = RunScope.Lifecycle.createKey<StringBuilder>()

fun RunScope.handleGradleOutput(line: String, isError: Boolean, onGradleEvent: (GradleAlert) -> Unit) {
    handleConsoleOutput(line, isError)

    if (line.startsWith(GRADLE_ERROR_PREFIX)) {
        onGradleEvent(GradleAlert.Error(line.removePrefix(GRADLE_ERROR_PREFIX)))
    } else if (line.startsWith(GRADLE_WARNING_PREFIX)) {
        onGradleEvent(GradleAlert.Warning(line.removePrefix(GRADLE_WARNING_PREFIX)))
    } else if (line.startsWith(GRADLE_TASK_PREFIX)) {
        onGradleEvent(GradleAlert.Task(line.removePrefix(GRADLE_TASK_PREFIX).substringBefore(' ')))
    } else if (line == "Change detected, executing build...") {
        onGradleEvent(GradleAlert.BuildRestarted)
    }
    // For the next two else statements, error messages appear sandwiched between "what went wrong:" and "try:" blocks.
    // We surface just the error message for now. We'll see in practice if that results in confusing output or not
    // based on user reports...
    else if (line == GRADLE_WHAT_WENT_WRONG) {
        data[WhatWentWrongKey] = StringBuilder()
    } else if (data.contains(WhatWentWrongKey)) {
        if (line.startsWith(GRADLE_TRY_PREFIX)) {
            data.remove(WhatWentWrongKey) {
                val sb = this
                // Remove a trailing newline, which separated previous error text from the
                // "* Try:" block below it.
                onGradleEvent(GradleAlert.Error(sb.toString().trimEnd()))
            }
        } else {
            data.getValue(WhatWentWrongKey).appendLine(line)
        }
    }
}

/**
 * Class which handles the collection and rendering of Gradle compile warnings and errors.
 */
class GradleAlertBundle(session: Session, private val pageSize: Int = 7) {
    private val alerts = session.liveListOf<GradleAlert>()
    private var hasFirstTaskRun by session.liveVarOf(false)
    private var startIndex by session.liveVarOf(0)
    private val maxIndex get() = (alerts.size - pageSize).coerceAtLeast(0)

    fun handleAlert(alert: GradleAlert) {
        if (alert is GradleAlert.BuildRestarted) {
            startIndex = 0
            alerts.clear()
        } else if (alert is GradleAlert.Task) {
            hasFirstTaskRun = true
        } else {
            alerts.add(alert)
        }
    }

    fun handleKey(key: Key): Boolean {
        var handled = true
        when(key) {
            Keys.HOME -> startIndex = 0
            Keys.END -> startIndex = maxIndex
            Keys.UP -> startIndex = (startIndex - 1).coerceAtLeast(0)
            Keys.PAGE_UP -> startIndex = (startIndex - pageSize).coerceAtLeast(0)
            Keys.DOWN -> startIndex = (startIndex + 1).coerceAtMost(maxIndex)
            Keys.PAGE_DOWN -> startIndex = (startIndex + pageSize).coerceAtMost(maxIndex)
            else -> handled = false
        }
        return handled
    }

    fun renderInto(renderScope: RenderScope) {
        renderScope.apply {
            if (!hasFirstTaskRun) {
                yellow { textLine("Output may seem to pause for a while if Kobweb needs to download / resolve dependencies.") }
                textLine()
            }
        }

        val numErrors = alerts.filterIsInstance<GradleAlert.Error>().size
        val numWarnings = alerts.filterIsInstance<GradleAlert.Warning>().size

        if (numErrors + numWarnings == 0) return

        renderScope.apply {
            yellow {
                text("Found $numErrors error(s) and $numWarnings warning(s).")
                if (numErrors > 0) {
                    text(" Please resolve errors to continue.")
                }
                textLine()
            }
            textLine()
            if (startIndex > 0) {
                textLine("... Press UP, PAGE UP, or HOME to see earlier errors.")
            }
            for (i in startIndex until (startIndex + pageSize)) {
                if (i >= alerts.size) break
                val alert = alerts[i]

                if (i > startIndex) textLine()
                text("${i + 1}: ")
                when (alert) {
                    is GradleAlert.Error -> red { textLine(alert.line) }
                    is GradleAlert.Warning -> yellow { textLine(alert.line) }
                    else -> error("Unexpected alert type: $alert")
                }
            }
            if (startIndex < maxIndex) {
                textLine("... Press DOWN, PAGE DOWN, or END to see later errors.")
            }
            textLine()
        }
    }
}
