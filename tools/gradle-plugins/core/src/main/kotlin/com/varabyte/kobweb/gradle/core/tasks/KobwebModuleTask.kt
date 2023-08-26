package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.common.path.toUnixSeparators
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.gradle.core.util.getBuildScripts
import com.varabyte.kobweb.gradle.core.util.getResourceFilesWithRoots
import com.varabyte.kobweb.gradle.core.util.getSourceFiles
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.io.File

/**
 * Base class for any task that needs to know about any source / resources related to Kobweb in the current module.
 */
abstract class KobwebModuleTask(@get:Internal val kobwebBlock: KobwebBlock, desc: String) : KobwebTask(desc) {
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE) // rerun if contents or path relative to project root changes
    fun getBuildScripts(): List<File> = project.getBuildScripts().toList()

    @Internal
    protected fun getSourceFilesJs(): List<File> = project.getSourceFiles(project.jsTarget).toList()

    @Internal
    protected fun getSourceFilesJvm(): List<File> =
        project.jvmTarget?.let { project.getSourceFiles(it).toList() } ?: emptyList()

    @Internal
    fun getResourceFilesJsWithRoots(): Sequence<RootAndFile> = project.getResourceFilesWithRoots(project.jsTarget)
        .filter { rootAndFile -> rootAndFile.relativeFile.toUnixSeparators().startsWith("${getPublicPath()}/") }

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
