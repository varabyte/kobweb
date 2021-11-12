@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.project.site.SiteData
import com.varabyte.kobweb.gradle.application.templates.createHtmlFile
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class KobwebGenerateSiteTask @Inject constructor(config: KobwebConfig, private val buildTarget: BuildTarget)
    : KobwebProjectTask(config, "Generate Kobweb code and resources for the frontend site") {

    @InputFiles
    fun getSourceFiles() = getSourceFilesJs()

    @InputFiles
    fun getResourceFiles() = getResourceFilesJs()

    @OutputDirectory
    fun getGenSrcDir(): File = config.getGenJsSrcRoot(project)

    @OutputDirectory
    fun getGenResDir(): File = config.getGenJsResRoot(project)

    @TaskAction
    fun execute() {
        val genSrcRoot = getGenSrcDir().also { it.mkdirs() }
        val genResRoot = getGenResDir().also { it.mkdirs() }

        with(SiteData.from(project.group.toString(), config.pagesPackage.get(), getSourceFiles())) {
            File(genSrcRoot, "main.kt").writeText(
                createMainFunction(
                    app,
                    // Sort by route as it makes the generated registration logic easier to follow
                    pages.sortedBy { it.route },
                    kobwebInits,
                    silkInits,
                    buildTarget,
                )
            )
        }

        File(genResRoot, getPublicPath()).let { publicRoot ->
            File(publicRoot, "index.html").writeText(
                createHtmlFile(
                    kobwebConf.site.title,
                    // TODO(Bug #7): Only specify font-awesome link if necessary
                    listOf("""<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" />"""),
                    kobwebConf.server.files.dev.script.substringAfterLast("/"),
                    buildTarget
                )
            )
        }
    }
}
