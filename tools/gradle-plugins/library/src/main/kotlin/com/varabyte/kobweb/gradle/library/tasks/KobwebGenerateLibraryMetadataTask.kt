package com.varabyte.kobweb.gradle.library.tasks

import com.varabyte.kobweb.gradle.core.metadata.LibraryMetadata
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.core.util.IndexHead
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_LIBRARY
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class KobwebGenerateLibraryMetadataTask :
    KobwebTask("Generate a library.json metadata file into this project's jar metadata, which identifies this artifact as a Kobweb library.") {

    @get:Nested
    abstract val indexHead: Property<IndexHead>

    @OutputDirectory
    fun getGenResDir() = projectLayout.buildDirectory.dir("generated/kobweb/library/metadata")

    @TaskAction
    fun execute() {
        val libraryMetadataFile = getGenResDir().get().file(KOBWEB_METADATA_LIBRARY)
        libraryMetadataFile.asFile.apply {
            parentFile.mkdirs()

            val headElements = indexHead.get().get().takeIf { it.isNotBlank() }
            writeText(
                Json.encodeToString(
                    LibraryMetadata(
                        LibraryMetadata.Index(
                            headElements = headElements
                        )
                    )
                )
            )
        }
    }
}
