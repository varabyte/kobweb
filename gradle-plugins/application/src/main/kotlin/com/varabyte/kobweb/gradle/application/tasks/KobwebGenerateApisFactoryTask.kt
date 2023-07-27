@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.KOBWEB_APP_METADATA_BACKEND
import com.varabyte.kobweb.gradle.application.templates.createApisFactoryImpl
import com.varabyte.kobweb.gradle.core.KOBWEB_METADATA_BACKEND
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import com.varabyte.kobweb.gradle.core.project.backend.BackendData
import com.varabyte.kobweb.gradle.core.project.backend.merge
import com.varabyte.kobweb.gradle.core.tasks.KobwebModuleTask
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import kotlinx.serialization.json.Json
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.provider.DefaultProvider
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class KobwebGenerateApisFactoryTask @Inject constructor(kobwebBlock: KobwebBlock) :
    KobwebModuleTask(kobwebBlock, "Generate Kobweb code for the server") {

    @InputFiles
    fun getCompileClasspath(): Provider<FileCollection> = project.jvmTarget?.let { jvmTarget ->
        @Suppress("UNCHECKED_CAST")
        project.configurations.named(jvmTarget.compileClasspath) as Provider<FileCollection>
    } ?: DefaultProvider { project.objects.fileCollection() }

    @InputFile
    fun getAppBackendMetadata() = project.layout.buildDirectory.file(KOBWEB_APP_METADATA_BACKEND)

    @OutputFile
    fun getGenApisFactoryFile() = kobwebBlock.getGenJvmSrcRoot(project).resolve("ApisFactoryImpl.kt")

    @TaskAction
    fun execute() {
        val backendData =
            mutableListOf(
                Json.decodeFromString(
                    BackendData.serializer(),
                    getAppBackendMetadata().get().asFile.readText()
                )
            ).apply {
                getCompileClasspath().get().files.forEach { file ->
                    file.searchZipFor(KOBWEB_METADATA_BACKEND) { bytes ->
                        add(Json.decodeFromString(BackendData.serializer(), bytes.decodeToString()))
                    }
                }
            }.merge()

        val apisFactoryFile = getGenApisFactoryFile()
        apisFactoryFile.parentFile.mkdirs()
        apisFactoryFile.writeText(createApisFactoryImpl(backendData))
    }
}
