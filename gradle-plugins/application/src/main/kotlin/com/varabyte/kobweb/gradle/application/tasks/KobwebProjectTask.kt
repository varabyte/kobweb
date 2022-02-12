@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.application.extensions.RootAndFile
import com.varabyte.kobweb.gradle.application.extensions.TargetPlatform
import com.varabyte.kobweb.gradle.application.extensions.getBuildScripts
import com.varabyte.kobweb.gradle.application.extensions.getResourceFilesWithRoots
import com.varabyte.kobweb.gradle.application.extensions.getSourceFiles
import com.varabyte.kobweb.project.conf.KobwebConfFile
import com.varabyte.kobweb.server.api.ServerState
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import java.io.File

fun ServerState.toDisplayText(): String {
    return "http://localhost:$port (PID = $pid)"
}

/**
 * Base-class for tasks that want convenient utility methods / properties providing insight into the current Kobweb
 * project.
 */
abstract class KobwebProjectTask(@get:Internal val kobwebBlock: KobwebBlock, desc: String) : KobwebTask(desc) {
    @get:Internal
    protected val kobwebConfFile = KobwebConfFile(kobwebProject.kobwebFolder)

    @get:Internal
    protected val kobwebConf =
        kobwebConfFile.content ?: throw GradleException("Could not find configuration file: ${this.path}")

    @InputFile
    fun getConfFile(): File = kobwebConfFile.path.toFile()

    @InputFiles
    fun getBuildScripts(): List<File> = project.getBuildScripts().toList()

    @Internal
    protected fun getSourceFilesJs(): List<File> = project.getSourceFiles(TargetPlatform.JS).toList()

    @Internal
    protected fun getSourceFilesJvm(): List<File> = project.getSourceFiles(TargetPlatform.JVM).toList()

    @Internal
    fun getResourceFilesJsWithRoots(): Sequence<RootAndFile> = project.getResourceFilesWithRoots(TargetPlatform.JS)
        .filter { rootAndFile -> rootAndFile.relativeFile.path.startsWith("${getPublicPath()}/") }

    @Internal
    protected fun getResourceFilesJs(): List<File> = getResourceFilesJsWithRoots().map { it.file }.toList()

    /**
     * The root package of all pages.
     *
     * Any composable function not under this root will be ignored, even if annotated by @Page.
     *
     * An initial '.' means this should be prefixed by the project group, e.g. ".pages" -> "com.example.pages"
     */
    @Input
    fun getPagesPackage(): String = kobwebBlock.pagesPackage.get()

    /**
     * The root package of all API handlers.
     *
     * Any handler not under this root will be ignored, even if annotated by @Api.
     *
     * An initial '.' means this should be prefixed by the project group, e.g. ".api" -> "com.example.api"
     */
    @Input
    fun getApiPackage(): String = kobwebBlock.apiPackage.get()

    /**
     * The path of public resources inside the project's resources folder, e.g. "public" ->
     * "src/jsMain/resources/public"
     */
    @Input
    fun getPublicPath(): String = kobwebBlock.publicPath.get()
}