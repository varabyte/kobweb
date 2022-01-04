@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.toUnixSeparators
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.extensions.index
import com.varabyte.kobweb.gradle.application.extensions.isDescendantOf
import com.varabyte.kobweb.gradle.application.project.site.SiteData
import com.varabyte.kobweb.gradle.application.templates.createIndexFile
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class KobwebGenerateSiteTask @Inject constructor(config: KobwebConfig, private val buildTarget: BuildTarget) :
    KobwebProjectTask(config, "Generate Kobweb code and resources for the frontend site") {

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
        getResourceFilesJsWithRoots()
                .mapNotNull{ rootAndFile -> rootAndFile.file.takeIf { !it.isDescendantOf(project.buildDir) && rootAndFile.relativeFile.toUnixSeparators() == "public/index.html"} }
                .singleOrNull()
                ?.let { indexFile ->
                    project.logger.error("$indexFile: You are not supposed to define this file yourself. Kobweb provides its own. Use the kobweb.index { ... } block if you need to modify the generated index file.")
                }

        val genSrcRoot = getGenSrcDir()
        val genResRoot = getGenResDir()

        with(
            SiteData.from(
                project.group.toString(),
                config.pagesPackage.get(),
                getSourceFiles(),
                GradleReporter(project.logger)
            )
        ) {
            genSrcRoot.mkdirs()
            File(genSrcRoot, "main.kt").writeText(createMainFunction(this, buildTarget))
        }

        File(genResRoot, getPublicPath()).let { publicRoot ->
            publicRoot.mkdirs()
            File(publicRoot, "index.html").writeText(
                createIndexFile(
                    kobwebConf.site.title,
                    config.index.head.get(),
                    // Our script will always exist at the root folder, so be sure to ground it,
                    // e.g. "example.js" -> "/example.js", so the root will be searched even if we're visiting a page in
                    // a subdirectory.
                    "/" + kobwebConf.server.files.dev.script.substringAfterLast("/"),
                    buildTarget
                )
            )
        }
    }
}