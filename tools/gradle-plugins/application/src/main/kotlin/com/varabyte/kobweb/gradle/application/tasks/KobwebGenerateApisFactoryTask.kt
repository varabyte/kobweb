package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.templates.createApisFactoryImpl
import com.varabyte.kobweb.project.backend.AppBackendData
import kotlinx.serialization.json.Json
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class KobwebGenerateApisFactoryTask @Inject constructor(private val appBlock: AppBlock) :
    KobwebGenerateTask("Generate Kobweb code for the server") {

    @get:InputFile
    abstract val appDataFile: RegularFileProperty

    @OutputDirectory // needs to be dir to be registered as a kotlin srcDir
    fun getGenApisFactoryFile() = appBlock.getGenJvmSrcRoot()

    @TaskAction
    fun execute() {
        val appBackendData = Json.decodeFromString<AppBackendData>(appDataFile.get().asFile.readText())
        val apisFactoryFile = getGenApisFactoryFile().get().asFile.resolve("ApisFactoryImpl.kt")
        apisFactoryFile.writeText(createApisFactoryImpl(appBackendData))
    }
}
