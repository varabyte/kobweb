package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.project.KobwebApplication
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal
import javax.inject.Inject

/**
 * Minimal base class for ensuring that the current task is grouped consistently with other Kobweb tasks.
 */
abstract class KobwebTask @Inject constructor(desc: String) : DefaultTask() {
    @get:Inject
    abstract val projectLayout: ProjectLayout

    /**
     * Request access to a [KobwebApplication] instance.
     *
     * This property should only be called on tasks that are part of a Kobweb application module, or else an exception
     * will be thrown.
     *
     * See also: [KobwebApplication] for more details.
     */
    @get:Internal
    val kobwebApplication get() = KobwebApplication(projectLayout.projectDirectory.asFile.toPath())

    protected fun KobwebBlock.FileGeneratingBlock.getGenJsSrcRoot(subDirectory: String? = null): Provider<Directory> {
        return genDir.flatMap { genDir ->
            projectLayout.buildDirectory.dir("$genDir${subDirectory?.let { "/$it" } ?: ""}/src/jsMain/kotlin")
        }
    }

    protected fun KobwebBlock.FileGeneratingBlock.getGenJsResRoot(subDirectory: String? = null): Provider<Directory> {
        return genDir.flatMap { genDir ->
            projectLayout.buildDirectory.dir("$genDir${subDirectory?.let { "/$it" } ?: ""}/src/jsMain/resources")
        }
    }

    protected fun KobwebBlock.FileGeneratingBlock.getGenJvmSrcRoot(): Provider<Directory> =
        genDir.flatMap { projectLayout.buildDirectory.dir("$it/src/jvmMain/kotlin") }

    init {
        group = "kobweb"
        description = desc
    }
}
