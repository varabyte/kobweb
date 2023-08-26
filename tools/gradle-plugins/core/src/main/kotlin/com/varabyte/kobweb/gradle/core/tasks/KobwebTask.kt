package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.project.KobwebApplication
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import javax.inject.Inject

/**
 * Minimal base class for ensuring that the current task is grouped consistently with other Kobweb tasks.
 */
abstract class KobwebTask @Inject constructor(desc: String) : DefaultTask() {
    /**
     * Request access to a [KobwebApplication] instance.
     *
     * This property should only be called on tasks that are part of a Kobweb application module, or else an exception
     * will be thrown.
     *
     * See also: [KobwebApplication] for more details.
     */
    @get:Internal
    val kobwebApplication get() = KobwebApplication(project.layout.projectDirectory.asFile.toPath())

    init {
        group = "kobweb"
        description = desc
    }
}