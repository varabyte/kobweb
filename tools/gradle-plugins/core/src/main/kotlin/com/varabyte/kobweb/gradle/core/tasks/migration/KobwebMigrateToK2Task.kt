package com.varabyte.kobweb.gradle.core.tasks.migration

import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.core.util.getBuildScripts
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.conf.KobwebConfFile
import org.gradle.api.tasks.TaskAction
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.relativeTo
import kotlin.io.path.writeText

private const val MIGRATION_DOC = "https://github.com/varabyte/kobweb/blob/main/docs/k2-migration.md"

// Migrate kobweb projects to Kotlin 2.0.10, which includes migrating to the new compose-compiler plugin:
// https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compiler.html
// We also remove the "org.jetbrains.compose" plugin from kobweb projects, as it is no longer necessary
// (and comes with unnecessary bloat) and instead add explicit catalog entries for the necessary compose dependencies
abstract class KobwebMigrateToK2Task :
    KobwebTask("Make an attempt to automatically migrate this Kobweb codebase to Kotlin 2.0") {

    @TaskAction
    fun execute() {
        // NOTE: This task is only registered to the root project
        val rootFile = project.projectDir.toPath()

        var numUpdatedFiles = 0

        fun Path.updateIfChanged(originalText: String, updatedText: String) {
            if (originalText != updatedText) {
                val relativeFile = this.relativeTo(rootFile)
                println("Updated $relativeFile")
                this.writeText(updatedText)
                numUpdatedFiles++
            }
        }

        val versionsFile = rootFile.resolve("gradle/libs.versions.toml")

        if (versionsFile.notExists()) {
            println("Version catalog file ($versionsFile) not found. Cannot perform migration.")
            println("See $MIGRATION_DOC for detailed K2 migration instructions.")
            return
        }

        versionsFile.takeIf { project == project.rootProject }?.let { file ->
            val originalText = file.readText()

            if ("kotlin = \"2.0." in originalText) {
                println("Project is already using Kotlin 2.0.")
                println("If you still want to run the `kobwebMigrateToK2` task, temporarily downgrade the Kotlin version to 1.9.23 and run the task again.")
                println("See $MIGRATION_DOC for K2 migration instructions if you have any issues.")
                return
            }

            val updatedText = originalText
                // support migration from all 1.9.2x versions, since users may not have updated to the bugfix releases
                .replace("kotlin = \"1.9.2[0-5]\"".toRegex(), "kotlin = \"2.0.10\"")
                .replace("jetbrains-compose = \"1.6.\\d*\"".toRegex(), "jetbrains-compose = \"1.6.11\"")
                .replace(
                    """jetbrains-compose = { id = "org.jetbrains.compose", version.ref = "jetbrains-compose" }""",
                    """
                    compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
                    jetbrains-compose = { id = "org.jetbrains.compose", version.ref = "jetbrains-compose" }
                    """.trimIndent()
                ).replace(
                    "[libraries]",
                    """
                    [libraries]
                    compose-html-core = { module = "org.jetbrains.compose.html:html-core", version.ref = "jetbrains-compose" }
                    compose-runtime = { module = "org.jetbrains.compose.runtime:runtime", version.ref = "jetbrains-compose" }
                    """.trimIndent()
                )

            file.updateIfChanged(originalText, updatedText)
        }

        // Add .kotlin to .gitignore
        // See: https://kotlinlang.org/docs/whatsnew20.html#new-directory-for-kotlin-data-in-gradle-projects
        rootFile.resolve(".gitignore").takeIf { it.exists() }?.let { file ->
            val originalText = file.readText()
            val updatedText = originalText
                .takeIf { ".kotlin" !in it }
                ?.plus(
                    """
                        # Kotlin ignores
                        .kotlin
                    """.trimIndent()
                ) ?: originalText

            file.updateIfChanged(originalText, updatedText)
        }

        val buildScripts = mutableListOf<Path>()
        val confFiles = mutableListOf<Path>()

        project.subprojects {
            val subProject = this
            pluginManager.withPlugin("com.varabyte.kobweb.application") {
                buildScripts.addAll(subProject.layout.getBuildScripts())
                KobwebFolder.fromChildPath(subProject.layout.projectDirectory.asFile.toPath())?.let {
                    confFiles.add(KobwebConfFile(it).path)
                }
            }
            pluginManager.withPlugin("com.varabyte.kobweb.library") {
                buildScripts.addAll(subProject.layout.getBuildScripts())
            }
        }

        buildScripts.forEach { file ->
            val originalText = file.readText()

            val updatedText = originalText
                .replace("alias(libs.plugins.jetbrains.compose)", "alias(libs.plugins.compose.compiler)")
                .replace("implementation(compose.runtime)", "implementation(libs.compose.runtime)")
                .replace("implementation(compose.html.core)", "implementation(libs.compose.html.core)")
                // This is not related to K2, but is a necessary and non-breaking removal, so we take care of it here
                // for convenience.
                .replace(""".*legacyRouteRedirectStrategy(\s*=\s*|.set\().*DISALLOW\)?""".toRegex(), "")
                // Remove default template doc comment as well
                .replace(
                    """
                    \s*// Only legacy sites need this set. Sites built after 0.16.0 should default to DISALLOW.
                    \s*// See https://github.com/varabyte/kobweb#legacy-routes for more information.
                    """.trimIndent().toRegex(RegexOption.MULTILINE), ""
                )

            file.updateIfChanged(originalText, updatedText)
        }

        confFiles.forEach { file ->
            val originalText = file.readText()
            // The only change is "dist" -> "kotlin-webpack" but we match against a larger path to avoid false positives
            val updatedText = originalText
                .replace("build/dist/js/developmentExecutable", "build/kotlin-webpack/js/developmentExecutable")
                .replace("build/dist/js/productionExecutable", "build/kotlin-webpack/js/productionExecutable")

            file.updateIfChanged(originalText, updatedText)
        }

        if (numUpdatedFiles == 0) {
            println("No files were updated. See $MIGRATION_DOC for K2 migration instructions if you have any issues.")
        } else {
            println()
            println("$numUpdatedFiles file(s) were updated.")
        }
    }
}
