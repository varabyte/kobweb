package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.gradle.core.util.getResourceFilesWithRoots
import org.gradle.api.tasks.Internal

/**
 * Base class for any task that needs to know about any source / resources related to Kobweb in the current module.
 */
abstract class KobwebModuleTask(@get:Internal val kobwebBlock: KobwebBlock, desc: String) : KobwebTask(desc) {
    @Internal
    fun getResourceFilesJsWithRoots(): Sequence<RootAndFile> = project.getResourceFilesWithRoots(project.jsTarget)
        .filter { rootAndFile -> rootAndFile.relativeFile.invariantSeparatorsPath.startsWith("public/") }
}
