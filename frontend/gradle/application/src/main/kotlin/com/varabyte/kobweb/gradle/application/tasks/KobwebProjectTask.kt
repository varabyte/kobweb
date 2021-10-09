@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.extensions.TargetPlatform
import com.varabyte.kobweb.gradle.application.extensions.getResourceFilesWithRoots
import com.varabyte.kobweb.gradle.application.extensions.getSourceFiles
import com.varabyte.kobweb.gradle.application.templates.createHtmlFile
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import com.varabyte.kobweb.project.KobwebProject
import com.varabyte.kobweb.project.conf.KobwebConfFile
import com.varabyte.kobweb.server.api.ServerState
import org.gradle.api.GradleException
import org.gradle.api.tasks.*
import java.io.File
import javax.inject.Inject

fun ServerState.toDisplayText(): String {
    return "http://localhost:$port (PID = $pid)"
}

/**
 * Base-class for tasks that want convenient utility methods / properties providing insight into the current Kobweb
 * project.
 */
abstract class KobwebProjectTask(@get:Internal val config: KobwebConfig, desc: String) : KobwebTask(desc) {
    @get:Internal val kobwebProject = KobwebProject()

    @InputFile
    fun getConfFile(): File = project.layout.projectDirectory.file(kobwebProject.kobwebFolder.resolve("conf.yaml").toString()).asFile

    @InputFiles
    fun getSourceFiles(): List<File> = project.getSourceFiles(TargetPlatform.JS).toList()

    @InputFiles
    fun getResourceFiles(): List<File> = project.getResourceFilesWithRoots(TargetPlatform.JS)
        .filter { rootAndFile -> rootAndFile.relativeFile.path.startsWith("${getPublicPath()}/") }
        .map { it.file }
        .toList()

    /**
     * The root package of all pages.
     *
     * Any composable function not under this root will be ignored, even if annotated by @Page.
     *
     * An initial '.' means this should be prefixed by the project group, e.g. ".pages" -> "com.example.pages"
     */
    @Input
    fun getPagesPackage(): String = config.pagesPackage.get()

    /**
     * The path of public resources inside the project's resources folder, e.g. "public" ->
     * "src/jsMain/resources/public"
     */
    @Input
    fun getPublicPath(): String = config.publicPath.get()
}