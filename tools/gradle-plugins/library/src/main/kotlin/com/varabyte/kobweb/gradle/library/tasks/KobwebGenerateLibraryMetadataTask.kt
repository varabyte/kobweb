package com.varabyte.kobweb.gradle.library.tasks

import com.varabyte.kobweb.gradle.core.metadata.LibraryMetadata
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_LIBRARY
import com.varabyte.kobweb.project.common.nonStrictDefault
import kotlinx.serialization.json.Json
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Trivial output, not worth caching.")
abstract class KobwebGenerateLibraryMetadataTask :
    KobwebTask("Generate a library.json metadata file into this project's jar metadata, which identifies this artifact as a Kobweb library.") {

    @get:Input
    abstract val indexHead: Property<String>

    /** This library's icon sets mapped to their locked versions, e.g. `"fa" to "6.5.2"`. */
    @get:Input
    abstract val iconSetVersions: MapProperty<String, String>

    @OutputDirectory
    fun getGenResDir() = projectLayout.buildDirectory.dir("generated/kobweb/library/metadata")

    @TaskAction
    fun execute() {
        val libraryMetadataFile = getGenResDir().get().file(KOBWEB_METADATA_LIBRARY)
        libraryMetadataFile.asFile.apply {
            parentFile.mkdirs()

            val headElements = indexHead.get().takeIf { it.isNotBlank() }
            writeText(
                Json.nonStrictDefault.encodeToString(
                    LibraryMetadata(
                        LibraryMetadata.Index(
                            headElements = headElements
                        ),
                        iconSets = iconSetVersions.get(),
                    )
                )
            )
        }
    }
}
