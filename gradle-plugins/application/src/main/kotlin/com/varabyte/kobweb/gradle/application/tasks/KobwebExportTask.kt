@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.github.kklisura.cdt.launch.ChromeArguments
import com.github.kklisura.cdt.launch.ChromeLauncher
import com.github.kklisura.cdt.services.ChromeService
import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.project.site.SiteData
import com.varabyte.kobweb.server.api.SiteLayout
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerStateFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jsoup.Jsoup
import java.io.File
import javax.inject.Inject

abstract class KobwebExportTask @Inject constructor(config: KobwebConfig, private val serverEnvironment: ServerEnvironment, private val siteLayout: SiteLayout) :
    KobwebProjectTask(config, "Export the Kobweb project into a static site") {

    @OutputDirectory
    fun getSiteDir(): File {
        return project.layout.projectDirectory.dir(kobwebConfFile.content!!.server.files.prod.siteRoot).asFile
    }

    private fun ChromeService.takeSnapshot(url: String): String {
        lateinit var snapshot: String

        val tab = createTab()
        val devToolsService = createDevToolsService(tab)
        val page = devToolsService.page
        val runtime = devToolsService.runtime
        page.onLoadEventFired {
            val evaluation = runtime.evaluate("document.documentElement.outerHTML")
            snapshot = Jsoup.parse(evaluation.result.value.toString()).toString()
            devToolsService.close()
        }
        page.enable()
        page.navigate(url)
        devToolsService.waitUntilClosed()

        return snapshot
    }

    private fun <T> T.toTriple() = Triple(this, this, this)
    private fun <T, S> Triple<T, T, T>.map(transform: (T) -> S) = Triple(transform(first), transform(second), transform(third))

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

        val (pagesRoot, resourcesRoot, systemRoot) = when(siteLayout) {
            SiteLayout.KOBWEB -> Triple("pages", "resources", "system").map { File(getSiteDir(), it) }
            SiteLayout.STATIC -> getSiteDir().toTriple()
        }

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

                pages
                    .map { it.route }
                    // Skip export routes with dynamic parts, as they are dynamically generated based on their URL
                    // anyway
                    .filter { !it.contains('{') }
                    .toSet()
                    .forEach { route ->
                        val snapshot = chromeService.takeSnapshot("http://localhost:$port$route")

                        var filePath = route.substringBeforeLast('/') + "/" +
                            (route.substringAfterLast('/').takeIf { it.isNotEmpty() } ?: "index") +
                            ".html"

                        // Drop the leading slash so we don't confuse File resolve logic
                        filePath = filePath.drop(1)
                        pagesRoot
                            .resolve(filePath)
                            .run {
                                parentFile.mkdirs()
                                writeText(snapshot)
                            }
                    }
            }
        }

        // Copy resources.
        // Note: The "index.html" file that comes from here is auto-generated and useful as a fallback for dynamic
        // export layouts but shouldn't be copied over in static layouts as those should only include pages explicitly
        // defined by the site.
        getResourceFilesJsWithRoots().forEach { rootAndFile ->
            // Drop the leading slash so we don't confuse File resolve logic
            val relativePath = rootAndFile.relativeFile.toString().substringAfter(getPublicPath()).drop(1)
            if (relativePath == "index.html" && siteLayout != SiteLayout.KOBWEB) return@forEach

            (if (relativePath != "index.html") resourcesRoot else systemRoot)
                .resolve(relativePath)
                .let { destFile ->
                    rootAndFile.file.copyTo(destFile, overwrite = true)
                }
        }

        val scriptFile = project.layout.projectDirectory.file(kobwebConf.server.files.dev.script).asFile
        run {
            val destFile = systemRoot.resolve(scriptFile.name)
            scriptFile.copyTo(destFile, overwrite = true)
        }

        if (serverEnvironment == ServerEnvironment.DEV) {
            val scriptMapFile = File("${scriptFile}.map")
            run {
                val destFile = systemRoot.resolve(scriptMapFile.name)
                scriptMapFile.copyTo(destFile, overwrite = true)
            }
        }

        // Kobweb servers are only supported by the Kobweb layout
        if (siteLayout == SiteLayout.KOBWEB) {
            // The api.jar is not guaranteed to exist -- not every project needs to have API routes defined.
            kobwebConf.server.files.dev.api.takeIf { it.isNotBlank() }?.let { apiFile ->
                val apiJarFile = project.layout.projectDirectory.file(apiFile).asFile
                if (apiJarFile.exists()) {
                    val destFile = systemRoot.resolve(apiJarFile.name)
                    apiJarFile.copyTo(destFile, overwrite = true)
                }
            }
        }
    }
}