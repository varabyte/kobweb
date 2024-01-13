package com.varabyte.kobweb.gradle.library.tasks

import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.core.metadata.LibraryMetadata
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.library.extensions.index
import com.varabyte.kobweb.gradle.library.extensions.library
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_LIBRARY
import kotlinx.html.head
import kotlinx.html.stream.createHTML
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class KobwebGenerateLibraryMetadataTask @Inject constructor(private val project: Project) :
    KobwebTask("Generate a library.json metadata file into this project's jar metadata, which identifies this artifact as a Kobweb library.") {

    @get:Inject
    abstract val projectLayout: ProjectLayout

    @OutputDirectory
    fun getGenResDir() = projectLayout.buildDirectory.dir("generated/kobweb/library/metadata")

    @TaskAction
    fun execute() {
        val libraryMetadataFile = getGenResDir().get().file(KOBWEB_METADATA_LIBRARY)
        libraryMetadataFile.asFile.apply {
            parentFile.mkdirs()

            val libraryBlock = project.kobwebBlock.library
            val headElements = libraryBlock.index.head.orNull?.takeIf { it.isNotEmpty() }
            writeText(
                Json.encodeToString(
                    LibraryMetadata(
                        LibraryMetadata.Index(
                            headElements = headElements?.let {
                                createHTML().head {
                                    it.forEach { element -> element() }
                                }
                            }
                        )
                    )
                )
            )
        }
    }
}
