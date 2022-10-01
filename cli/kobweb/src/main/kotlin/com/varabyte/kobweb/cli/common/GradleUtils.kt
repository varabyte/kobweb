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
import com.varabyte.kotter.runtime.render.RenderScope

class KobwebGradle(private val env: ServerEnvironment) {
    private fun gradlew(vararg args: String): Process {
        val finalArgs = args.toMutableList()
        finalArgs.add("--stacktrace")
        if (env == ServerEnvironment.PROD) {
            // When in production, we don't want to leave a daemon running around hoarding resources unecessarily
            finalArgs.add("--no-daemon")
        }

        return Runtime.getRuntime().gradlew(*finalArgs.toTypedArray())
    }

    fun startServer(enableLiveReloading: Boolean, siteLayout: SiteLayout): Process {
        val args = mutableListOf("-PkobwebEnv=$env", "-PkobwebRunLayout=$siteLayout", "kobwebStart")
        if (enableLiveReloading) {
            args.add("-t")
        }
        return gradlew(*args.toTypedArray())
    }

    fun stopServer(): Process {
        return gradlew("kobwebStop")
    }

    fun export(siteLayout: SiteLayout): Process {
        // Even if we are exporting a non-Kobweb layout, we still want to start up a dev server using a Kobweb layout so
        // it looks for the source files in the right place.
        return gradlew("-PkobwebReuseServer=false", "-PkobwebEnv=DEV", "-PkobwebRunLayout=KOBWEB", "-PkobwebBuildTarget=RELEASE", "-PkobwebExportLayout=$siteLayout", "kobwebExport")
    }
}

private const val GRADLE_ERROR_PREFIX = "e: "
private const val GRADLE_WARNING_PREFIX = "w: "
private const val GRADLE_TASK_PREFIX = "> Task :"

sealed interface GradleAlert {
    class Warning(val line: String) : GradleAlert
    class Error(val line: String) : GradleAlert
    class Task(val task: String) : GradleAlert
    object BuildRestarted : GradleAlert
}

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
