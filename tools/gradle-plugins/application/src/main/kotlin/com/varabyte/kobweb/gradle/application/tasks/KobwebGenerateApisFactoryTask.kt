package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.app
import com.varabyte.kobweb.gradle.application.templates.createApisFactoryImpl
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_BACKEND
import com.varabyte.kobweb.project.backend.BackendData
import com.varabyte.kobweb.project.backend.merge
import kotlinx.serialization.json.Json
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class KobwebGenerateApisFactoryTask @Inject constructor(kobwebBlock: KobwebBlock) :
    KobwebGenerateTask(kobwebBlock, "Generate Kobweb code for the server") {
    @get:Optional
    @get:InputFile
    abstract val kspGenFile: RegularFileProperty

    @get:InputFiles
    abstract val compileClasspath: ConfigurableFileCollection

    @OutputDirectory // needs to be dir to be registered as a kotlin srcDir
    fun getGenApisFactoryFile() = kobwebBlock.app.getGenJvmSrcRoot()

    @TaskAction
    fun execute() {
        val backendData = buildList {
            kspGenFile.orNull?.let {
                add(Json.decodeFromString<BackendData>(it.asFile.readText()))
            }
            compileClasspath.forEach { file ->
                file.searchZipFor(KOBWEB_METADATA_BACKEND) { bytes ->
                    add(Json.decodeFromString<BackendData>(bytes.decodeToString()))
                }
            }
        }.merge(throwError = { throw GradleException(it) })

        val apisFactoryFile = getGenApisFactoryFile().get().asFile.resolve("ApisFactoryImpl.kt")
        apisFactoryFile.writeText(createApisFactoryImpl(backendData))
    }
}
