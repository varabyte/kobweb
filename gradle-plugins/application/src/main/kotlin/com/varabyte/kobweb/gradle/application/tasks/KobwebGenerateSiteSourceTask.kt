@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.application.project.site.SiteData
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class KobwebGenerateSiteSourceTask @Inject constructor(
    config: KobwebBlock,
    @get:Input val buildTarget: BuildTarget
) : KobwebProjectTask(config, "Generate extra code for this Kobweb project") {

    private fun getGenSrcDir(): File = kobwebBlock.getGenJsSrcRoot(project)

    @InputFiles
    fun getSourceFiles() = run {
        // Don't let stuff we output force ourselves to run again
        val genMainFile = getGenMainFile()
        getSourceFilesJs()
            .filter { it.absolutePath != genMainFile.absolutePath }
    }

    @OutputFile
    fun getGenMainFile() = File(getGenSrcDir(), "main.kt")

    @TaskAction
    fun execute() {
        val routePrefix = RoutePrefix(kobwebConf.site.routePrefix)
        with(
            SiteData.from(
                project.group.toString(),
                kobwebBlock.pagesPackage.get(),
                getSourceFiles(),
                GradleReporter(project.logger)
            )
        ) {
            val mainFile = getGenMainFile()
            mainFile.parentFile.mkdirs()
            mainFile.writeText(createMainFunction(this, kobwebBlock.appGlobals.get(), routePrefix, buildTarget))
        }
    }
}