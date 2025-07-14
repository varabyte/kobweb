package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_BACKEND
import com.varabyte.kobweb.project.backend.AppBackendData
import com.varabyte.kobweb.project.backend.BackendData
import com.varabyte.kobweb.project.backend.merge
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

// NOTE: This task in meant as an internal API so it does not inherit from KobwebTask
/**
 * Collect all backend app data from the current site and all library dependencies, writing the result to
 * [appDataFile].
 *
 * This is done so that multiple tasks can read the same values from a single, cached file. Those tasks should take
 * `appDataFile` as an input and then deserialize it in their execute method:
 *
 * ```
 * // Configuring the task
 * myBackendAppDataUsingTask.configure {
 *   appDataFile.set(kobwebCacheAppBackendDataTask.flatMap { it.appDataFile })
 * }
 *
 * // Inside the task
 * @get:InputFile
 * abstract val appDataFile: RegularFileProperty
 *
 * @TaskAction
 * fun execute() {
 *   val appData = Json.decodeFromString<AppBackendData>(appDataFile.get().asFile.readText())
 *   // ...
 * }
 * ```
 */
abstract class KobwebCacheAppBackendDataTask : DefaultTask() {
    init {
        description =
            "Search the project and merge all backend app data, saving it into a file, at which point it can be looked up by downstream tasks that need it."
    }

    @get:Optional
    @get:InputFile
    abstract val appBackendMetadataFile: RegularFileProperty

    @get:InputFiles
    abstract val compileClasspath: ConfigurableFileCollection

    @get:OutputFile
    abstract val appDataFile: RegularFileProperty

    @TaskAction
    fun execute() {
        // metadataFile will normally be found but might not exist for a project which JUST enabled the jvm backend
        val appBackendDataFile = appBackendMetadataFile.orNull?.asFile ?: run {
            appDataFile.get().asFile.writeText(Json.encodeToString(AppBackendData()))
            return
        }

        val appBackendData = Json.decodeFromString<AppBackendData>(appBackendDataFile.readText())
        val mergedBackendData = buildList {
            add(appBackendData.backendData)
            compileClasspath.forEach { file ->
                file.searchZipFor(KOBWEB_METADATA_BACKEND) { bytes ->
                    add(Json.decodeFromString<BackendData>(bytes.decodeToString()))
                }
            }
        }
            .merge(throwError = { throw GradleException(it) })

        appDataFile.get().asFile.writeText(Json.encodeToString(AppBackendData(appBackendData.apiInterceptorMethod, mergedBackendData)))
    }
}
