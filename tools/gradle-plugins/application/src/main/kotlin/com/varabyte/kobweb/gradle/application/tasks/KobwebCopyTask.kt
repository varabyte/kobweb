package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

/**
 * Common base class for all "Kobweb copy" tasks, with convenience methods for processing & copying files.
 */
@DisableCachingByDefault(because = "Base task; up to children to decide caching strategy for themselves.")
abstract class KobwebCopyTask(desc: String) : KobwebTask(desc) {
    @get:Inject
    abstract val fileSystemOperations: FileSystemOperations

    @get:Inject
    abstract val objectFactory: ObjectFactory

    @get:Inject
    abstract val archiveOperations: ArchiveOperations

    @get:Input
    abstract val publicPath: Property<String>

    // We intentionally don't use @Classpath. We just treat this as a list of files to pay attention to if something has
    // changed without needing to inspect jar contents. Also, our artifacts are Kotlin/JS, not Kotlin/JVM.
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val runtimeClasspath: ConfigurableFileCollection
}
