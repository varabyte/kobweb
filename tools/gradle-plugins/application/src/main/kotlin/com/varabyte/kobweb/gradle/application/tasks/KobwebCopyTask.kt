package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.path.invariantSeparatorsPath
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_MODULE
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.FileTree
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.util.PatternSet
import org.gradle.work.DisableCachingByDefault
import java.io.File
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

class RootAndFile(val root: File, val file: File) {
    val relativeFile get() = file.relativeTo(root)
}

private val kobwebModulePattern = PatternSet().apply {
    include(KOBWEB_METADATA_MODULE)
}

private fun FileTree.toKobwebOutputByPattern(patternSet: PatternSet, jar: File): List<Pair<File, RootAndFile>> {
    val fileTree = this
    if (fileTree.matching(kobwebModulePattern).isEmpty) return emptyList()

    return buildList {
        fileTree.matching(patternSet).visit {
            if (this.isDirectory) return@visit
            val root = File(file.absolutePath.invariantSeparatorsPath.removeSuffix(relativePath))
            add(jar to RootAndFile(root, file))
        }
    }
}

fun FileCollection.toKobwebOutputByPattern(
    objectFactory: ObjectFactory,
    archiveOperations: ArchiveOperations,
    patternSet: PatternSet,
): List<Pair<File, RootAndFile>> {
    return this.flatMap { jar ->
        if (jar.isDirectory) {
            objectFactory.fileTree().from(jar).toKobwebOutputByPattern(patternSet, jar)
        } else {
            try {
                archiveOperations.zipTree(jar).toKobwebOutputByPattern(patternSet, jar)
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}
