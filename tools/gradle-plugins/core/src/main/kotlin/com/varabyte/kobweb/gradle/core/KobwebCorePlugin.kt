package com.varabyte.kobweb.gradle.core

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.extensions.YarnLockChangedStrategy
import com.varabyte.kobweb.gradle.core.extensions.createYarnBlock
import com.varabyte.kobweb.gradle.core.extensions.yarn
import com.varabyte.kobweb.gradle.core.tasks.KobwebGenerateModuleMetadataTask
import com.varabyte.kobweb.gradle.core.tasks.migration.KobwebMigrateToCssStyleTask
import com.varabyte.kobweb.gradle.core.tasks.migration.KobwebMigrateToK2Task
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebCorePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val rootProject = project.rootProject

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

        project.tasks.register("kobwebGenerateModuleMetadata", KobwebGenerateModuleMetadataTask::class.java)
    }
}

// We don't simply do this ourselves in the core plugin apply method because they don't apply to the worker plugin.
// TODO: We should consider creating a new module which is just for "application" and "library" plugins.
fun Project.registerKobwebMigrationTasks() {
    tasks.register("kobwebMigrateToCssStyle", KobwebMigrateToCssStyleTask::class.java)
    if ("kobwebMigrateToK2" !in rootProject.tasks.names) {
        rootProject.tasks.register("kobwebMigrateToK2", KobwebMigrateToK2Task::class.java) {
            notCompatibleWithConfigurationCache("not worth it")
        }
    }
}
