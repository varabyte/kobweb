@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.github.kklisura.cdt.launch.ChromeArguments
import com.github.kklisura.cdt.launch.ChromeLauncher
import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.project.site.SiteData
import com.varabyte.kobweb.server.api.ServerStateFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jsoup.Jsoup
import java.io.File
import javax.inject.Inject

abstract class KobwebExportTask @Inject constructor(config: KobwebConfig) :
    KobwebProjectTask(config, "Export the Kobweb project into a static site") {

    @OutputDirectory
    fun getSiteDir(): File {
        return project.layout.projectDirectory.dir(kobwebConfFile.content!!.server.files.prod.siteRoot).asFile
    }

    @TaskAction
    fun execute() {
        // Sever should be running since "kobwebStart" is a prerequisite for this task
        val port = ServerStateFile(kobwebProject.kobwebFolder).content!!.port

        val siteData = SiteData.from(
            project.group.toString(),
            getPagesPackage(),
            getSourceFilesJs(),
            GradleReporter(project.logger)
        )
        siteData.pages.takeIf { it.isNotEmpty() }?.let { pages ->
            ChromeLauncher().use { launcher ->
                // NOTE: Normally "no-sandbox" is NOT recommended for security reasons. However, this option is
                // necessary when this task runs as root, as Chrome complains otherwise, but this scenario is common in
                // containers. (It's expected that `kobweb export` will often get run as part of a container image built
                // in the Cloud). Since the lifetime of the browser is short, and it exists only for doing these exports
                // of our own code before getting shut down again, the fact we are disabling the sandbox here is not a
                // concern (as far as I can think through).
                val chromeService = launcher.launch(
                    ChromeArguments.defaults(true).additionalArguments("no-sandbox", true).build()
                )

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

        getResourceFilesJsWithRoots().forEach { rootAndFile ->
            val relativePath = rootAndFile.relativeFile.toString().substringAfter(getPublicPath())
            // The auto-generated "/index.html" file should be used as a fallback if the user visits an invalid path
            val destFile = File(
                getSiteDir(),
                if (relativePath != "/index.html") "resources$relativePath" else "system$relativePath"
            )
            rootAndFile.file.copyTo(destFile, overwrite = true)
        }

        val scriptFile = project.layout.projectDirectory.file(kobwebConf.server.files.dev.script).asFile
        run {
            val destFile = File(getSiteDir(), "system/${scriptFile.name}")
            scriptFile.copyTo(destFile, overwrite = true)
        }

        val scriptMapFile = File("${scriptFile}.map")
        run {
            val destFile = File(getSiteDir(), "system/${scriptMapFile.name}")
            scriptMapFile.copyTo(destFile, overwrite = true)
        }

        // The api.jar is not guaranteed to exist -- not every project needs to have API routes defined.
        val apiJarFile = project.layout.projectDirectory.file(kobwebConf.server.files.dev.api).asFile
        if (apiJarFile.exists()) {
            val destFile = File(getSiteDir(), "system/${apiJarFile.name}")
            apiJarFile.copyTo(destFile, overwrite = true)
        }
    }
}