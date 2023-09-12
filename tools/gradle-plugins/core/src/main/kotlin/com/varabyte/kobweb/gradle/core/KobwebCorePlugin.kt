package com.varabyte.kobweb.gradle.core

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.extensions.YarnLockChangedStrategy
import com.varabyte.kobweb.gradle.core.extensions.createYarnBlock
import com.varabyte.kobweb.gradle.core.extensions.yarn
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.JvmTarget
import com.varabyte.kobweb.gradle.core.kmp.buildTargets
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobweb.gradle.core.util.hasJsDependencyNamed
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebCorePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val rootProject = project.rootProject
        val versionCatalogPath = "gradle/libs.versions.toml"

        run {
            val groupIdKobweb = "com.varabyte.kobweb"
            val groupIdKobwebx = "com.varabyte.kobwebx"
            // Original artifact ID (grouped under com.varabyte.kobweb) to coordinate
            val relocatedArtifacts = mapOf(
                "kobweb-silk-widgets" to "$groupIdKobweb:silk-widgets",
                "kobweb-silk-icons-fa" to "$groupIdKobwebx:silk-icons-fa",
                "kobweb-silk-icons-mdi" to "$groupIdKobwebx:silk-icons-mdi",
            )

            val migrateDepsName = "kobwebMigrateDeps"
            if (rootProject.tasks.findByName(migrateDepsName) != null) return@run

            val migrateDepsTask = rootProject.tasks.register(migrateDepsName) {
                inputs.file(versionCatalogPath)
                outputs.file(versionCatalogPath)
                group = "kobweb"
                description =
                    "Migrate any Kobweb dependencies found in `$versionCatalogPath` that should be relocated to new coordinates."

                doLast {
                    rootProject.layout.projectDirectory.file(versionCatalogPath).asFile.takeIf { it.exists() }
                        ?.let { tomlFile ->
                            val originalText = tomlFile.readText()
                            var updatedText = originalText
                            relocatedArtifacts.forEach { (legacyArtifactId, newCoordinate) ->
                                updatedText =
                                    updatedText.replace("$groupIdKobweb:$legacyArtifactId", newCoordinate)
                            }
                            if (originalText != updatedText) {
                                println(
                                    "Your `$versionCatalogPath` has been updated to use the latest Kobweb dependency names."
                                )
                                tomlFile.writeText(updatedText)
                            } else {
                                println(
                                    "We did not find any Kobweb dependencies in `$versionCatalogPath` that need to be migrated."
                                )
                            }
                        }
                        ?: println(
                            "We could not find `$versionCatalogPath` so we cannot try to migrate dependencies."
                        )
                }
            }

            project.gradle.taskGraph.whenReady {
                // Warn the user about any relocated deps, if used, unless we're already running the task which will
                // migrate them. If we show the warning then, it feels like the task failed even as it succeeded.
                if (!this.hasTask(migrateDepsTask.get())) {
                    relocatedArtifacts.forEach { (legacyArtifactId, newCoordinate) ->
                        if (project.hasJsDependencyNamed(legacyArtifactId)) {
                            project.logger.warn("w: The dependency `$groupIdKobweb:$legacyArtifactId` has been renamed to `$newCoordinate`. Please migrate to the new name. You can run `./gradlew $migrateDepsName` to attempt to do this automatically. Failing to migrate will become an error in a future version of Kobweb.")
                        }
                    }
                }
            }

        }

        // A `kobweb` block is not used directly here in the core plugin but is provided as a foundational building
        // block for both library and application plugins.
        val kobwebBlock = project.extensions.create<KobwebBlock>("kobweb")
        kobwebBlock.createYarnBlock()

        rootProject.plugins.withType<YarnPlugin>().configureEach {
            try {
                rootProject.extensions.configure<YarnRootExtension> {
                    val yarnBlock = kobwebBlock.yarn
                    yarnLockMismatchReport = when (yarnBlock.lockChangedStrategy.get()) {
                        is YarnLockChangedStrategy.Fail -> YarnLockMismatchReport.FAIL
                        else -> YarnLockMismatchReport.WARNING
                    }

                    yarnLockAutoReplace = yarnBlock.lockChangedStrategy.get() == YarnLockChangedStrategy.Regenerate
                    reportNewYarnLock =
                        (yarnBlock.lockChangedStrategy.get() as? YarnLockChangedStrategy.Fail)?.rejectCreatingNewLock
                            ?: false
                }
            } catch (ex: NoSuchMethodError) {
                throw GradleException("This version of Kobweb requires a newer Kotlin version than what this project is using. Please refer to https://github.com/varabyte/kobweb/blob/main/COMPATIBILITY.md")
            }
        }

        // use matching instead of named as tasks may not exist yet
        project.buildTargets.withType<KotlinJsIrTarget>().configureEach {
            val jsTarget = JsTarget(this)
            project.kotlin.sourceSets.named(jsTarget.mainSourceSet) {
                resources.srcDirs(project.tasks.matching { it.name == jsTarget.kspKotlin })
            }
        }
        project.buildTargets.withType<KotlinJvmTarget>().configureEach {
            val jvmTarget = JvmTarget(this)
            project.kotlin.sourceSets.named(jvmTarget.mainSourceSet) {
                resources.srcDirs(project.tasks.matching { it.name == jvmTarget.kspKotlin })
            }
        }
    }
}
