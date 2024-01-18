package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.gradle.core.util.getResourceFilesWithRoots
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

/**
 * Base class for any task that needs to know about any source / resources related to Kobweb in the current module.
 */
abstract class KobwebModuleTask(@get:Internal val kobwebBlock: KobwebBlock, desc: String) : KobwebTask(desc) {
    @Internal
    fun getResourceFilesJsWithRoots(): Sequence<RootAndFile> = project.getResourceFilesWithRoots(project.jsTarget)
        .filter { rootAndFile -> rootAndFile.relativeFile.invariantSeparatorsPath.startsWith("${getPublicPath()}/") }

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
