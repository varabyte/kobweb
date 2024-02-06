package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_FRONTEND
import com.varabyte.kobweb.project.frontend.AppData
import com.varabyte.kobweb.project.frontend.FrontendData
import com.varabyte.kobweb.project.frontend.merge
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

// NOTE: This task in meant as an internal API so it does not inherit from KobwebTask
abstract class KobwebCacheFrontendDataTask : DefaultTask() {
    init {
        description = "Search the project and merge all frontend data, saving it into a file, at which point it can be looked up by downstream tasks that need it."
    }

    @get:InputFile
    abstract val appFrontendMetadataFile: RegularFileProperty

    @get:InputFiles
    abstract val compileClasspath: ConfigurableFileCollection

    @get:OutputFile
    abstract val frontendDataFile: RegularFileProperty

    @TaskAction
    fun execute() {
        val appData = Json.decodeFromString<AppData>(appFrontendMetadataFile.get().asFile.readText())
        val frontendData = buildList {
            add(appData.frontendData)
            compileClasspath.forEach { file ->
                file.searchZipFor(KOBWEB_METADATA_FRONTEND) { bytes ->
                    add(Json.decodeFromString<FrontendData>(bytes.decodeToString()))
                }
            }
        }
            .merge(throwError = { throw GradleException(it) })

        frontendDataFile.get().asFile.writeText(Json.encodeToString(frontendData))
    }
}
