package com.varabyte.kobweb.gradle.core

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.extensions.YarnLockChangedStrategy
import com.varabyte.kobweb.gradle.core.extensions.createYarnBlock
import com.varabyte.kobweb.gradle.core.extensions.yarn
import com.varabyte.kobweb.gradle.core.tasks.KobwebGenerateModuleMetadataTask
import com.varabyte.kobweb.gradle.core.util.KOBWEB_CONFIGURE_COMPOSE_COMPILER
import com.varabyte.kobweb.gradle.core.util.configureComposeCompiler
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.configuration.BuildFeatures
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import javax.inject.Inject

@Suppress("unused") // KobwebCorePlugin is found by Gradle via reflection
class KobwebCorePlugin @Inject constructor(private val buildFeatures: BuildFeatures) : Plugin<Project> {
    override fun apply(project: Project) {
        val rootProject = project.rootProject

        // A `kobweb` block is not used directly here in the core plugin but is provided as a foundational building
        // block for both library and application plugins.
        val kobwebBlock = project.extensions.create<KobwebBlock>("kobweb")
        kobwebBlock.createYarnBlock()

        // The official guidance for configuring the yarn plugin is "isolated projects" incompatible. Eventually, we
        // will need to give users guidance on how to migrate their code. However, it doesn't look like that will be
        // ready until the Kotlin 2.5.x releases start rolling out at the earliest, so it's hard to know what to suggest
        // at this point. If push came to shove today, users should probably declare the kotlin multiplatform plugin
        // (with `apply false`) in their root build script and configure the `YarnRootExtension` object there.
        //
        // For now, we'll ensure that Kobweb doesn't show up in "isolated project" error reports. (Users won't be using
        // isolated projects at this point anyway because KGP is absolutely not "isolated project" compatible right now.
        // However, if curious people are collecting error reports early, at least we can avoid showing up in them at
        //  this point.)
        if (!buildFeatures.isolatedProjects.active.get()) {
            rootProject.plugins.withType<YarnPlugin>().configureEach {
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
            }
        }

        project.tasks.register("kobwebGenerateModuleMetadata", KobwebGenerateModuleMetadataTask::class.java)

        if (project.providers.gradleProperty(KOBWEB_CONFIGURE_COMPOSE_COMPILER).getOrElse("true").toBoolean()) {
            project.configureComposeCompiler()
        }
    }
}
