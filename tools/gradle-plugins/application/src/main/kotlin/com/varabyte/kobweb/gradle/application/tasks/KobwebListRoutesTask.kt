package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.project.frontend.AppFrontendData
import com.varabyte.kobweb.project.frontend.PageEntry
import kotlinx.serialization.json.Json
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction


/**
 * Print all routes for the current site to the console.
 *
 * This includes dynamic routes too, e.g. "/users/{user}/posts/{post}". If you'd like to filter those out, add the
 * following code to your build script:
 *
 * ```
 * tasks.named<KobwebListRoutesTask>("kobwebListRoutes").configure {
 *     excludeDynamicRoutes.set(true)
 * }
 * ```
 */
abstract class KobwebListRoutesTask : KobwebTask("Enumerate all routes for your site managed by Kobweb, printing them out to the console") {
    @get:InputFile
    abstract val appDataFile: RegularFileProperty

    @get:Input
    @get:Optional
    abstract val excludeDynamicRoutes: Property<Boolean>

    @TaskAction
    fun execute() {
        val excludeDynamicRoutes = excludeDynamicRoutes.getOrElse(false)
        val routes = Json.decodeFromString<AppFrontendData>(appDataFile.get().asFile.readText()).frontendData.pages
            .asSequence()
            .map { it.route }
            .filter { !(excludeDynamicRoutes && it.contains('{')) }
            .sorted()
            .toList()

        if (routes.isNotEmpty()) {
            println("Your site defines the following routes:")
            routes.forEach { route -> println("  $route") }
        } else {
            println("No routes have been defined in your project yet.")
        }
    }
}
