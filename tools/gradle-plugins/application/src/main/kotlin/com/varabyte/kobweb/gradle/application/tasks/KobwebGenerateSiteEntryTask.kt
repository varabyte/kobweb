package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.templates.SilkSupport
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_FRONTEND
import com.varabyte.kobweb.project.frontend.AppData
import com.varabyte.kobweb.project.frontend.FrontendData
import kotlinx.serialization.json.Json
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
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
    private val appBlock: AppBlock,
    kobwebBlock: KobwebBlock,
) : KobwebGenerateTask(kobwebBlock, "Generate entry code (i.e. main.kt) for this Kobweb project") {
    @get:Input
    val cleanUrls: Provider<Boolean> = appBlock.cleanUrls

    @get:Input
    val globals: Provider<Map<String, String>> = appBlock.globals

    @get:InputFile
    abstract val kspGenFile: RegularFileProperty

    @get:InputFiles
    abstract val compileClasspath: ConfigurableFileCollection

    @get:Input
    abstract val hasKobwebSilkDependency: Property<Boolean>

    @get:Input
    abstract val hasSilkFoundationDependency: Property<Boolean>

    @OutputDirectory // needs to be dir to be registered as a kotlin srcDir
    fun getGenMainFile() = appBlock.getGenJsSrcRoot()

    @TaskAction
    fun execute() {
        val appData = Json.decodeFromString<AppData>(kspGenFile.get().asFile.readText())
        val mainFile = getGenMainFile().get().asFile.resolve("main.kt")

        val libData = buildList {
            compileClasspath.files.forEach { file ->
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
                    hasKobwebSilkDependency.get() -> SilkSupport.FULL
                    hasSilkFoundationDependency.get() -> SilkSupport.FOUNDATION
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
