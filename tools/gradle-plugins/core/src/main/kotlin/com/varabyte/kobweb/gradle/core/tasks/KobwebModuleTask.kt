package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.gradle.core.util.getResourceFilesWithRoots
import com.varabyte.kobweb.gradle.core.util.getSourceFilesWithRoots
import org.gradle.api.tasks.Internal

/**
 * Base class for any task that needs to know about any source / resources related to Kobweb in the current module.
 */
abstract class KobwebModuleTask(desc: String) : KobwebTask(desc) {
    @Internal
    fun getResourceFilesJsWithRoots(): Sequence<RootAndFile> = project.getResourceFilesWithRoots(project.jsTarget)
        .filter { rootAndFile -> rootAndFile.relativeFile.invariantSeparatorsPath.startsWith("$publicPath/") }

    @Internal
    fun getSourceFilesJsWithRoots(): Sequence<RootAndFile> = project.getSourceFilesWithRoots(project.jsTarget)

    /**
     * The path of public resources inside the project's resources folder, e.g. "public" ->
     * "src/jsMain/resources/public"
     */
    private val publicPath = project.kobwebBlock.publicPath.get()
}
