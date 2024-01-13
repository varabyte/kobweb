@file:Suppress("DEPRECATION")

package com.varabyte.kobweb.gradle.library.tasks

import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.core.metadata.LibraryIndexMetadata
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.library.extensions.index
import com.varabyte.kobweb.gradle.library.extensions.library
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_INDEX
import kotlinx.html.head
import kotlinx.html.stream.createHTML
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

// Leaving this class around for a little while so if the library author upgrades Kobweb before their users do, they
// will still work for a while.
// We are moving to library.json because that is symmetric with worker.json (created by worker projects)
@Deprecated("Migrated to KobwebGenerateLibraryMetadataTask")
abstract class KobwebGenerateIndexMetadataTask @Inject constructor(private val project: Project) :
    KobwebTask("Generate an index.json metadata file into this project's jar metadata, which contains details from the kobweb index block.") {

    @get:Inject
    abstract val projectLayout: ProjectLayout

    @OutputDirectory
    fun getGenResDir() = projectLayout.buildDirectory.dir("generated/kobweb/library/index/metadata")

    @TaskAction
    fun execute() {
        val libraryMetadataFile = getGenResDir().get().file(KOBWEB_METADATA_INDEX)
        libraryMetadataFile.asFile.apply {
            parentFile.mkdirs()

            val libraryBlock = project.kobwebBlock.library
            val headElements = libraryBlock.index.head.orNull?.takeIf { it.isNotEmpty() } ?: return@apply

            writeText(
                Json.encodeToString(
                    LibraryIndexMetadata(
                        headElements = createHTML().head {
                            headElements.forEach { element -> element() }
                        }
                    )
                )
            )
        }
    }
}
