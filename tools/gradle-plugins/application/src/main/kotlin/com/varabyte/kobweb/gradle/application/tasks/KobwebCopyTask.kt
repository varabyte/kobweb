package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.path.invariantSeparatorsPath
import com.varabyte.kobweb.gradle.core.metadata.ModuleMetadata
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_MODULE
import kotlinx.serialization.json.Json
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

    private val kobwebModulePattern = PatternSet().apply {
        include(KOBWEB_METADATA_MODULE)
    }

    // Modules built with newer versions of Kobweb may add fields we don't know about
    private val moduleMetadataJson = Json { ignoreUnknownKeys = true }

    /**
     * A file found inside a Kobweb module (i.e. a jar or directory containing a Kobweb module.json file).
     */
    protected class KobwebModuleFile(val jar: File, val metadata: ModuleMetadata, val rootAndFile: RootAndFile)

    private fun FileTree.toKobwebOutputByPattern(
        jar: File,
        createPatternSet: (ModuleMetadata) -> PatternSet,
    ): List<KobwebModuleFile> {
        val fileTree = this
        val moduleFile = fileTree.matching(kobwebModulePattern).files.firstOrNull() ?: return emptyList()
        val metadata = moduleMetadataJson.decodeFromString(ModuleMetadata.serializer(), moduleFile.readText())

        return buildList {
            fileTree.matching(createPatternSet(metadata)).visit {
                if (this.isDirectory) return@visit
                val root =
                    File(file.absolutePath.invariantSeparatorsPath.removeSuffix(relativePath))
                add(KobwebModuleFile(jar, metadata, RootAndFile(root, file)))
            }
        }
    }

    /**
     * Find all files inside the Kobweb modules in this collection that match the pattern created for each module.
     *
     * The pattern is created per module since it may depend on the module's metadata, e.g. on its public path.
     */
    protected fun FileCollection.toKobwebOutputByPattern(
        createPatternSet: (ModuleMetadata) -> PatternSet,
    ): List<KobwebModuleFile> {
        return this.flatMap { jar ->
            if (jar.isDirectory) {
                objectFactory.fileTree().from(jar).toKobwebOutputByPattern(jar, createPatternSet)
            } else {
                try {
                    archiveOperations.zipTree(jar).toKobwebOutputByPattern(jar, createPatternSet)
                } catch (_: Exception) {
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
