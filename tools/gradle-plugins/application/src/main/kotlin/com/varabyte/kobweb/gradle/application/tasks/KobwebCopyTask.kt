package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.path.invariantSeparatorsPath
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_MODULE
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.FileTree
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.util.PatternSet
import java.io.File
import javax.inject.Inject

/**
 * Common base class for all "Kobweb copy" tasks, with convenience methods for processing & copying files.
 */
abstract class KobwebCopyTask(desc: String) : KobwebTask(desc) {
    @get:Inject
    abstract val fileSystemOperations: FileSystemOperations

    @get:Inject
    abstract val objectFactory: ObjectFactory

    @get:Inject
    abstract val archiveOperations: ArchiveOperations

    @get:Input
    abstract val publicPath: Property<String>

    private val kobwebModulePattern = PatternSet().apply {
        include(KOBWEB_METADATA_MODULE)
    }

    private fun FileTree.toKobwebOutputByPattern(patternSet: PatternSet, jar: File): List<Pair<File, RootAndFile>> {
        val fileTree = this
        if (fileTree.matching(kobwebModulePattern).isEmpty) return emptyList()

        return buildList {
            fileTree.matching(patternSet).visit {
                if (this.isDirectory) return@visit
                val root =
                    File(file.absolutePath.invariantSeparatorsPath.removeSuffix(relativePath))
                add(jar to RootAndFile(root, file))
            }
        }
    }

    protected fun Configuration.toKobwebOutputByPattern(patternSet: PatternSet): List<Pair<File, RootAndFile>> {
        return this.flatMap { jar ->
            if (jar.isDirectory) {
                objectFactory.fileTree().from(jar).toKobwebOutputByPattern(patternSet, jar)
            } else {
                try {
                    archiveOperations.zipTree(jar).toKobwebOutputByPattern(patternSet, jar)
                } catch (ex: Exception) {
                    // NOTE: I used to catch ZipException here, but it became GradleException at some point?? So
                    // let's just be safe and block all exceptions here. It sucks if this task crashes here because
                    // not being able to unzip a non-zip file is not really a big deal.

                    // It's possible to get a classpath file that's not a jar -- npm dependencies are like this --
                    // at which point the file isn't a zip nor a directory. Such dependencies will never contain
                    // Kobweb resources, so we don't care about them. Just skip 'em!
                    emptyList()
                }
            }
        }
    }
}
