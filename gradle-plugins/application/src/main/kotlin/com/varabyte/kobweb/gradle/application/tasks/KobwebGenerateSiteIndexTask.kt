@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.common.toUnixSeparators
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.application.extensions.index
import com.varabyte.kobweb.gradle.application.extensions.isDescendantOf
import com.varabyte.kobweb.gradle.application.templates.createIndexFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.util.prefixIfNot
import java.io.File
import javax.inject.Inject

abstract class KobwebGenerateSiteIndexTask @Inject constructor(config: KobwebBlock, private val buildTarget: BuildTarget) :
    KobwebProjectTask(config, "Generate an index.html file for this Kobweb project") {

    private fun getGenResDir(): File = kobwebBlock.getGenJsResRoot(project)

    @InputFiles
    fun getResourceFiles() = run {
        // Don't let stuff we output force ourselves to run again
        val genIndexFile = getGenIndexFile()
        getResourceFilesJs()
            .filter { it.absolutePath != genIndexFile.absolutePath }
    }

    @OutputFile
    fun getGenIndexFile() = File(File(getGenResDir(), getPublicPath()), "index.html")

    @TaskAction
    fun execute() {
        getResourceFilesJsWithRoots()
                .mapNotNull{ rootAndFile -> rootAndFile.file.takeIf { !it.isDescendantOf(project.buildDir) && rootAndFile.relativeFile.toUnixSeparators() == "public/index.html"} }
                .singleOrNull()
                ?.let { indexFile ->
                    project.logger.error("$indexFile: You are not supposed to define this file yourself. Kobweb provides its own. Use the kobweb.index { ... } block if you need to modify the generated index file.")
                }

        val routePrefix = RoutePrefix(kobwebConf.site.routePrefix)
         getGenIndexFile().let { indexFile ->
            indexFile.parentFile.mkdirs()
            indexFile.writeText(
                createIndexFile(
                    kobwebConf.site.title,
                    kobwebBlock.index.head.get(),
                    // Our script will always exist at the root folder, so be sure to ground it,
                    // e.g. "example.js" -> "/example.js", so the root will be searched even if we're visiting a page in
                    // a subdirectory.
                    routePrefix.prepend(kobwebConf.server.files.dev.script.substringAfterLast("/").prefixIfNot("/")),
                    buildTarget
                )
            )
        }
    }
}