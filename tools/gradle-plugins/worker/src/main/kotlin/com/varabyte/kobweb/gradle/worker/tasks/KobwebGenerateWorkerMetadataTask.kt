package com.varabyte.kobweb.gradle.worker.tasks

import com.varabyte.kobweb.gradle.core.metadata.WorkerMetadata
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_WORKER
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class KobwebGenerateWorkerMetadataTask :
    KobwebTask("Generate a worker.json metadata file into this project's jar metadata, which identifies this artifact as a Kobweb worker.") {

    @get:Inject
    abstract val projectLayout: ProjectLayout

    @OutputDirectory
    fun getGenResDir() = projectLayout.buildDirectory.dir("generated/kobweb/worker/metadata")

    @TaskAction
    fun execute() {
        val workerMetadataFile = getGenResDir().get().file(KOBWEB_METADATA_WORKER)
        workerMetadataFile.asFile.apply {
            parentFile.mkdirs()
            writeText(Json.encodeToString(WorkerMetadata()))
        }
    }
}
