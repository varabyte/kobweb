package com.varabyte.kobweb.gradle.library.tasks

import com.varabyte.kobweb.common.path.invariantSeparatorsPath
import com.varabyte.kobweb.gradle.core.metadata.LibraryMetadata
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.core.util.toKobwebOutputByPattern
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_LIBRARY
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_WORKER_SUBFOLDER
import kotlinx.serialization.json.Json
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = "Trivial output, not worth caching.")
abstract class KobwebGenerateLibraryMetadataTask :
    KobwebTask("Generate a library.json metadata file into this project's jar metadata, which identifies this artifact as a Kobweb library.") {
    @get:Inject
    abstract val archiveOperations: ArchiveOperations

    @get:Inject
    abstract val objectFactory: ObjectFactory

    @get:Input
    abstract val indexHead: Property<String>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val runtimeClasspath: ConfigurableFileCollection

    @OutputDirectory
    fun getGenResDir() = projectLayout.buildDirectory.dir("generated/kobweb/library/metadata")

    @TaskAction
    fun execute() {
        val workerOutputsFilesPattern = PatternSet().apply {
            include("$KOBWEB_METADATA_WORKER_SUBFOLDER/**")
        }
        val workers = runtimeClasspath.toKobwebOutputByPattern(objectFactory, archiveOperations, workerOutputsFilesPattern)
            .map { (_, rootAndFile) ->
                rootAndFile.relativeFile.invariantSeparatorsPath.removePrefix("$KOBWEB_METADATA_WORKER_SUBFOLDER/")
            }
            .distinct()
            .sorted()
        val libraryMetadataFile = getGenResDir().get().file(KOBWEB_METADATA_LIBRARY)
        libraryMetadataFile.asFile.apply {
            parentFile.mkdirs()

            val headElements = indexHead.get().takeIf { it.isNotBlank() }
            writeText(
                Json.encodeToString(
                    LibraryMetadata(
                        LibraryMetadata.Index(
                            headElements = headElements
                        ),
                        workers = workers,
                    )
                )
            )
        }
    }
}
