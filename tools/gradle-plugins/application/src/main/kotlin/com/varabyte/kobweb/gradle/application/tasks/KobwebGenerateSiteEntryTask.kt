@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.extensions.app
import com.varabyte.kobweb.gradle.application.templates.SilkSupport
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.tasks.KobwebModuleTask
import com.varabyte.kobweb.gradle.core.util.hasTransitiveJsDependencyNamed
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_FRONTEND
import com.varabyte.kobweb.project.conf.KobwebConf
import com.varabyte.kobweb.project.frontend.AppData
import com.varabyte.kobweb.project.frontend.FrontendData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class KobwebGenerateSiteEntryTask @Inject constructor(
    private val kobwebConf: KobwebConf,
    kobwebBlock: KobwebBlock,
    @get:Input val buildTarget: BuildTarget,
) : KobwebModuleTask(kobwebBlock, "Generate entry code (i.e. main.kt) for this Kobweb project") {
    @get:InputFile
    abstract val kspGenFile: RegularFileProperty

    @InputFiles
    fun getCompileClasspath() = project.configurations.named(project.jsTarget.compileClasspath)

    @OutputDirectory // needs to be dir to be registered as a kotlin srcDir
    fun getGenMainFile() = kobwebBlock.getGenJsSrcRoot<AppBlock>(project)

    @TaskAction
    fun execute() {
        val appData = Json.decodeFromString<AppData>(kspGenFile.get().asFile.readText())
        val routePrefix = RoutePrefix(kobwebConf.site.routePrefix)
        val mainFile = getGenMainFile().resolve("main.kt")

        val libData = buildList {
            getCompileClasspath().get().files.forEach { file ->
                file.searchZipFor(KOBWEB_METADATA_FRONTEND) { bytes ->
                    add(Json.decodeFromString<FrontendData>(bytes.decodeToString()))
                }
            }
        }

        mainFile.writeText(
            createMainFunction(
                appData,
                libData,
                when {
                    project.hasTransitiveJsDependencyNamed("kobweb-silk") -> SilkSupport.FULL
                    project.hasTransitiveJsDependencyNamed("silk-foundation") -> SilkSupport.FOUNDATION
                    else -> SilkSupport.NONE
                },
                kobwebBlock.app,
                routePrefix,
                buildTarget
            )
        )
    }
}
