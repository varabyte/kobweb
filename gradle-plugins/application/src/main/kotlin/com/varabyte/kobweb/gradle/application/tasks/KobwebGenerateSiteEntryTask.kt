@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.KOBWEB_APP_METADATA_FRONTEND
import com.varabyte.kobweb.gradle.application.extensions.app
import com.varabyte.kobweb.gradle.application.project.app.AppData
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import com.varabyte.kobweb.gradle.core.KOBWEB_METADATA_FRONTEND
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.project.frontend.FrontendData
import com.varabyte.kobweb.gradle.core.tasks.KobwebModuleTask
import com.varabyte.kobweb.gradle.core.util.hasTransitiveJsDependencyNamed
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.project.conf.KobwebConf
import kotlinx.serialization.json.Json
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class KobwebGenerateSiteEntryTask @Inject constructor(
    private val kobwebConf: KobwebConf,
    kobwebBlock: KobwebBlock,
    @get:Input val buildTarget: BuildTarget
) : KobwebModuleTask(kobwebBlock, "Generate entry code (i.e. main.kt) for this Kobweb project") {

    @InputFiles
    fun getCompileClasspath() = project.configurations.named(project.jsTarget.compileClasspath)

    @InputFile
    fun getAppMetadataFrontend() = project.buildDir.resolve(KOBWEB_APP_METADATA_FRONTEND)

    @OutputFile
    fun getGenMainFile() = kobwebBlock.getGenJsSrcRoot(project).resolve("main.kt")

    @TaskAction
    fun execute() {
        val appData = Json.decodeFromString(AppData.serializer(), getAppMetadataFrontend().readText())
        val routePrefix = RoutePrefix(kobwebConf.site.routePrefix)
        val mainFile = getGenMainFile()
        mainFile.parentFile.mkdirs()

        val libData = mutableListOf<FrontendData>().apply {
            getCompileClasspath().get().files.forEach { file ->
                file.searchZipFor(KOBWEB_METADATA_FRONTEND) { bytes ->
                    add(Json.decodeFromString(FrontendData.serializer(), bytes.decodeToString()))
                }
            }
        }

        mainFile.writeText(
            createMainFunction(
                appData,
                libData,
                project.hasTransitiveJsDependencyNamed("kobweb-silk"),
                kobwebBlock.app,
                routePrefix,
                buildTarget
            )
        )
    }
}