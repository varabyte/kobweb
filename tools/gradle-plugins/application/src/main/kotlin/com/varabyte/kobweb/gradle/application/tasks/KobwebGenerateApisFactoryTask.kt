@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.templates.createApisFactoryImpl
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import com.varabyte.kobweb.gradle.core.tasks.KobwebModuleTask
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_BACKEND
import com.varabyte.kobweb.project.backend.BackendData
import com.varabyte.kobweb.project.backend.merge
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.provider.DefaultProvider
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class KobwebGenerateApisFactoryTask @Inject constructor(kobwebBlock: KobwebBlock) :
    KobwebModuleTask(kobwebBlock, "Generate Kobweb code for the server") {
    @get:InputFiles // "files" since it may not exist
    abstract val kspGenFile: ListProperty<File>

    @InputFiles
    fun getCompileClasspath(): Provider<FileCollection> = project.jvmTarget?.let { jvmTarget ->
        @Suppress("UNCHECKED_CAST")
        project.configurations.named(jvmTarget.compileClasspath) as Provider<FileCollection>
    } ?: DefaultProvider { project.objects.fileCollection() }

    @OutputDirectory // needs to be dir to be registered as a kotlin srcDir
    fun getGenApisFactoryFile() = kobwebBlock.getGenJvmSrcRoot<AppBlock>(project)

    @TaskAction
    fun execute() {
        val backendData = buildList {
            kspGenFile.get().singleOrNull()?.takeIf { it.exists() }?.let {
                add(Json.decodeFromString<BackendData>(it.readText()))
            }
            getCompileClasspath().get().files.forEach { file ->
                file.searchZipFor(KOBWEB_METADATA_BACKEND) { bytes ->
                    add(Json.decodeFromString<BackendData>(bytes.decodeToString()))
                }
            }
        }.merge(throwError = { throw GradleException(it) })

        val apisFactoryFile = getGenApisFactoryFile().resolve("ApisFactoryImpl.kt")
        apisFactoryFile.writeText(createApisFactoryImpl(backendData))
    }
}
