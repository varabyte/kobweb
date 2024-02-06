package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.project.frontend.FrontendData
import kotlinx.serialization.json.Json
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

abstract class KobwebListRoutesTask : KobwebTask("Enumerate all routes for your site managed by Kobweb") {
    @get:InputFile
    abstract val frontendDataFile: RegularFileProperty

    @TaskAction
    fun execute() {
        val pageEntries = Json.decodeFromString<FrontendData>(frontendDataFile.asFile.get().readText()).pages
        if (pageEntries.isNotEmpty()) {
            println("Your site defines the following routes:")
            pageEntries.map { it.route }.sorted().forEach { route ->
                println("  $route")
            }
        } else {
            println("No routes have been defined in your project yet.")
        }
    }
}
