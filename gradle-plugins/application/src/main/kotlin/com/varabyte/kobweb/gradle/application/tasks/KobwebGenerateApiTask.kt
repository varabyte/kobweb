@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.application.project.api.ApiData
import com.varabyte.kobweb.gradle.application.templates.createApisFactoryImpl
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class KobwebGenerateApiTask @Inject constructor(kobwebBlock: KobwebBlock) :
    KobwebProjectTask(kobwebBlock, "Generate Kobweb code for the server") {

    private fun getGenDir() = kobwebBlock.getGenJvmSrcRoot(project)

    @InputFiles
    fun getSourceFiles() = run {
        // Don't let stuff we output force ourselves to run again
        val genApisFactoryFile = getGenApisFactoryFile()
        getSourceFilesJvm()
            .filter { it.absolutePath != genApisFactoryFile.absolutePath }
    }

    @OutputFile
    fun getGenApisFactoryFile() = File(getGenDir(), "ApisFactoryImpl.kt")

    @TaskAction
    fun execute() {
        with(
            ApiData.from(
                project.group.toString(),
                kobwebBlock.apiPackage.get(),
                getSourceFilesJvm(),
                GradleReporter(project.logger)
            )
        ) {
            val apisFactoryFile = getGenApisFactoryFile()
            apisFactoryFile.parentFile.mkdirs()
            apisFactoryFile.writeText(createApisFactoryImpl(this))
        }
    }
}