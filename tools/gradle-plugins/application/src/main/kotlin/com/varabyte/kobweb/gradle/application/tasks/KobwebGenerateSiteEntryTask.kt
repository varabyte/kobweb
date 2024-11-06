package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.navigation.BasePath
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.templates.SilkSupport
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import com.varabyte.kobweb.gradle.core.util.hasDependencyNamed
import com.varabyte.kobweb.project.conf.KobwebConf
import com.varabyte.kobweb.project.conf.Server
import com.varabyte.kobweb.project.frontend.AppData
import kotlinx.serialization.json.Json
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

class KobwebGenSiteEntryConfInputs(
    @get:Nested val redirects: List<Redirect>,
) {
    class Redirect(
        @get:Input val from: String,
        @get:Input val to: String
    )

    constructor(kobwebConf: KobwebConf) : this(kobwebConf.server.redirects.map { Redirect(it.from, it.to) })
}

abstract class KobwebGenerateSiteEntryTask @Inject constructor(
    private val appBlock: AppBlock,
    @get:Input val basePath: String,
    @get:Input val buildTarget: BuildTarget,
    @get:Nested val confInputs: KobwebGenSiteEntryConfInputs,
) : KobwebGenerateTask("Generate entry code (i.e. main.kt) for this Kobweb project") {
    @get:Input
    val cleanUrls: Provider<Boolean> = appBlock.cleanUrls

    @get:Input
    val globals: Provider<Map<String, String>> = appBlock.globals

    @get:InputFile
    abstract val appDataFile: RegularFileProperty

    @get:Internal
    abstract val dependencies: ListProperty<ResolvedDependencyResult>

    @get:Input
    val silkSupport: Provider<SilkSupport>
        get() = dependencies.hasDependencyNamed("com.varabyte.kobweb:kobweb-silk")
            .zip(dependencies.hasDependencyNamed("com.varabyte.kobweb:silk-foundation")) { left, right ->
                if (left) SilkSupport.FULL else if (right) SilkSupport.FOUNDATION else SilkSupport.NONE
            }

    @OutputDirectory // needs to be dir to be registered as a kotlin srcDir
    fun getGenMainFile() = appBlock.getGenJsSrcRoot()

    @TaskAction
    fun execute() {
        val appData = Json.decodeFromString<AppData>(appDataFile.asFile.get().readText())
        val mainFile = getGenMainFile().get().asFile.resolve("main.kt")

        mainFile.writeText(
            createMainFunction(
                appData,
                silkSupport.get(),
                globals.get(),
                cleanUrls.get(),
                BasePath(basePath),
                confInputs.redirects.map { Server.Redirect(it.from, it.to) },
                buildTarget,
            )
        )
    }
}
