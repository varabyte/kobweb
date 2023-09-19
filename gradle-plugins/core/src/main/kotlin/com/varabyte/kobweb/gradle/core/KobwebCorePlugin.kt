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

        // A `kobweb` block is not used directly here in the core plugin but is provided as a foundational building
        // block for both library and application plugins.
        val kobwebBlock = project.extensions.create<KobwebBlock>("kobweb")
        kobwebBlock.createYarnBlock()

        project.rootProject.plugins.withType<YarnPlugin>().configureEach {
            try {
                project.rootProject.extensions.configure<YarnRootExtension> {
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

        // Kobweb applications and libraries both put stuff in generated folders, which the Kotlin project should be
        // aware of.
        val genDir = kobwebBlock.genDir.get()
        project.buildTargets.withType<KotlinJsIrTarget>().configureEach {
            val jsTarget = JsTarget(this)

            project.kotlin.sourceSets.named(jsTarget.mainSourceSet) {
                kotlin.srcDir(project.layout.buildDirectory.dir("$genDir${jsTarget.srcSuffix}"))
                resources.srcDir(project.layout.buildDirectory.dir("$genDir${jsTarget.resourceSuffix}"))
            }
        }
        project.buildTargets.withType<KotlinJvmTarget>().configureEach {
            val jvmTarget = JvmTarget(this)

            project.kotlin.sourceSets.named(jvmTarget.mainSourceSet) {
                kotlin.srcDir(project.layout.buildDirectory.dir("$genDir${jvmTarget.srcSuffix}"))
                resources.srcDir(project.layout.buildDirectory.dir("$genDir${jvmTarget.resourceSuffix}"))
            }
        }

        project.afterEvaluate {
            val groupId = "com.varabyte.kobweb"
            val relocatedArtifacts = run {
                listOf(
                    "kobweb-silk-widgets",
                    "kobweb-silk-icons-fa",
                    "kobweb-silk-icons-mdi"
                ).associateWith { legacyArtifactId -> legacyArtifactId.removePrefix("kobweb-") }
            }
            relocatedArtifacts.forEach { legacyArtifactId, newArtifactId ->
                if (project.hasJsDependencyNamed(legacyArtifactId)) {
                    logger.warn("w: The dependency `$groupId:$legacyArtifactId` has been renamed to `$groupId:$newArtifactId`. Please migrate your dependency declaration to the new name. The correct dependency is being used for now, but this will become an error in a future version of Kobweb.")
                }
            }
        }
    }
}
