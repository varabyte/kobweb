package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.app
import com.varabyte.kobweb.gradle.application.templates.SilkSupport
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.util.hasTransitiveJsDependencyNamed
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_FRONTEND
import com.varabyte.kobweb.project.frontend.AppData
import com.varabyte.kobweb.project.frontend.FrontendData
import kotlinx.serialization.json.Json
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class KobwebGenerateSiteEntryTask @Inject constructor(
    @get:Input val routePrefix: String,
    @get:Input val buildTarget: BuildTarget,
    kobwebBlock: KobwebBlock,
) : KobwebGenerateTask(kobwebBlock, "Generate entry code (i.e. main.kt) for this Kobweb project") {
    @get:Input
    val cleanUrls: Provider<Boolean> = kobwebBlock.app.cleanUrls

    @get:Input
    val globals: Provider<Map<String, String>> = kobwebBlock.app.globals

    @get:InputFile
    abstract val kspGenFile: RegularFileProperty

    @get:InputFiles
    abstract val compileClasspath: ConfigurableFileCollection

    @OutputDirectory // needs to be dir to be registered as a kotlin srcDir
    fun getGenMainFile() = kobwebBlock.app.getGenJsSrcRoot()

    @TaskAction
    fun execute() {
        val appData = Json.decodeFromString<AppData>(kspGenFile.get().asFile.readText())
        val mainFile = getGenMainFile().get().asFile.resolve("main.kt")

        val libData = buildList {
            compileClasspath.forEach { file ->
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
                globals.get(),
                cleanUrls.get(),
                RoutePrefix(routePrefix),
                buildTarget
            )
        )
    }
}
