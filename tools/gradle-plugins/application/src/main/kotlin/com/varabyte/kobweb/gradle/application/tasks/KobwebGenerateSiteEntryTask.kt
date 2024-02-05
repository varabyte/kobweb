package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.extensions.app
import com.varabyte.kobweb.gradle.application.templates.SilkSupport
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.util.hasTransitiveJsDependencyNamed
import com.varabyte.kobweb.project.frontend.AppData
import kotlinx.serialization.json.Json
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
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
    @get:Optional
    val legacyRouteRedirectStrategy: Provider<AppBlock.LegacyRouteRedirectStrategy> =
        kobwebBlock.app.legacyRouteRedirectStrategy

    @get:Input
    val globals: Provider<Map<String, String>> = kobwebBlock.app.globals

    @get:InputFile
    abstract val appDataFile: RegularFileProperty

    @OutputDirectory // needs to be dir to be registered as a kotlin srcDir
    fun getGenMainFile() = kobwebBlock.app.getGenJsSrcRoot()

    @TaskAction
    fun execute() {
        val appData = Json.decodeFromString<AppData>(appDataFile.asFile.get().readText())
        val mainFile = getGenMainFile().get().asFile.resolve("main.kt")

        mainFile.writeText(
            createMainFunction(
                appData,
                when {
                    project.hasTransitiveJsDependencyNamed("kobweb-silk") -> SilkSupport.FULL
                    project.hasTransitiveJsDependencyNamed("silk-foundation") -> SilkSupport.FOUNDATION
                    else -> SilkSupport.NONE
                },
                globals.get(),
                cleanUrls.get(),
                RoutePrefix(routePrefix),
                buildTarget,
                legacyRouteRedirectStrategy.getOrElse(
                    if (buildTarget == BuildTarget.DEBUG) AppBlock.LegacyRouteRedirectStrategy.WARN else AppBlock.LegacyRouteRedirectStrategy.ALLOW
                )
            )
        )
    }
}
