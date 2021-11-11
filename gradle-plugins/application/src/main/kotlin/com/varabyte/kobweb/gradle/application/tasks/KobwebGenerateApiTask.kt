@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.project.api.ApiData
import com.varabyte.kobweb.gradle.application.templates.createApisFactoryImpl
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class KobwebGenerateApiTask @Inject constructor(config: KobwebConfig)
    : KobwebProjectTask(config, "Generate Kobweb code for the server") {

    @InputFiles
    fun getSourceFiles() = getSourceFilesJvm()

    @OutputDirectory
    fun getGenDir(): File = config.getGenJvmSrcRoot(project)

    @TaskAction
    fun execute() {
        val getSrcRoot = getGenDir()

        with(ApiData.from(project.group.toString(), config.apiPackage.get(), getSourceFilesJvm())) {
            getSrcRoot.mkdirs()
            File(getSrcRoot, "ApisFactoryImpl.kt").writeText(
                // Sort values as it makes the generated registration logic easier to follow
                createApisFactoryImpl(
                    apiMethods.sortedBy { entry -> entry.route },
                    initMethods,
                )
            )
        }
    }
}
