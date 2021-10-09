@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.github.kklisura.cdt.launch.ChromeLauncher
import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.server.api.ServerStateFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jsoup.Jsoup
import java.io.File
import javax.inject.Inject

abstract class KobwebExportTask @Inject constructor(config: KobwebConfig)
    : KobwebProjectTask(config, "Export the Kobweb project into a static site") {

    @OutputDirectory
    fun getSiteDir(): File = project.layout.projectDirectory.dir(config.siteDir.get()).asFile

    @TaskAction
    fun execute() {
        // Sever should be running since "kobwebStart" is a prerequisite for this task
        val port = ServerStateFile(kobwebProject.kobwebFolder).content!!.port

        val projectData = kobwebProject.parseData(project.group.toString(), getPagesPackage(), getSourceFiles())
        projectData.pages.takeIf { it.isNotEmpty() }?.let { pages ->
            val launcher = ChromeLauncher()
            val chromeService = launcher.launch(true)

            pages.forEach { pageEntry ->
                val tab = chromeService.createTab()
                val devToolsService = chromeService.createDevToolsService(tab)
                val page = devToolsService.page
                val runtime = devToolsService.runtime
                page.onLoadEventFired { _ ->
                    val evaluation = runtime.evaluate("document.documentElement.outerHTML")
                    val filePath = pageEntry.route.substringBeforeLast('/') + "/" +
                            (pageEntry.route.substringAfterLast('/').takeIf { it.isNotEmpty() } ?: "index") +
                            ".html"
                    val prettyHtml = Jsoup.parse(evaluation.result.value.toString()).toString()
                    File(getSiteDir(), "pages$filePath").run {
                        parentFile.mkdirs()
                        writeText(prettyHtml)
                    }
                    devToolsService.close()
                }
                page.enable()
                page.navigate("http://localhost:$port${pageEntry.route}")
                devToolsService.waitUntilClosed()
            }
        }
    }
}