package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.project.frontend.AppFrontendData
import kotlinx.serialization.json.Json
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * Create a special public dev folder, useful to add information that a dev server can check.
 *
 * These files will be excluded during export, so they will never get exposed to a prod server.
 */
abstract class KobwebCreatePublicDevFilesTask @Inject constructor(
    private val appBlock: AppBlock,
) : KobwebTask("Create dev-only files and put them in the `public` folder so a dev server can see them.") {
    @get:InputFile
    abstract val appDataFile: RegularFileProperty

    @get:Input
    abstract val publicPath: Property<String>

    @OutputDirectory
    fun getGenResDir() = appBlock.getGenJsResRoot("dev-public")

    private fun getDevFilesRoot() = getGenResDir().get().asFile.resolve(publicPath.get()).resolve("_kobweb/dev")

    @TaskAction
    fun execute() {
        getDevFilesRoot().mkdirs()

        // Create a list of known static routes, so dev servers running in static layout mode can reject requests to
        // dynamic routes.
        run {
            val routes = Json.decodeFromString<AppFrontendData>(appDataFile.get().asFile.readText())
                .routes(excludeDynamicRoutes = true)
            val routesFile = getDevFilesRoot().resolve("static-routes.txt")
            routesFile.createNewFile()
            routesFile.writeText(routes.joinToString("\n"))
        }
    }
}
