package com.varabyte.kobweb.gradle.core.tasks

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.processors.TokenProcessor
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class KobwebGenerateMetadataTask<T>(kobwebBlock: KobwebBlock, desc: String) :
    KobwebProcessSourcesTask(kobwebBlock, desc) {

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE) // rerun if contents or path relative to project root changes
    abstract fun getSourceFiles(): List<File>

    @OutputFile
    abstract fun getGeneratedMetadataFile(): File

    protected abstract fun createProcessor(): TokenProcessor<T>
    protected abstract fun encodeToString(value: T): String

    @TaskAction
    fun execute() {
        val data = process(
            getSourceFiles(),
            createProcessor()
        )

        val outputFile = getGeneratedMetadataFile()
        outputFile.parentFile.mkdirs()
        outputFile.writeText(encodeToString(data))
    }
}
