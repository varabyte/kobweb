package com.varabyte.kobweb.gradle.library

import com.varabyte.kobweb.gradle.core.KobwebCorePlugin
import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import com.varabyte.kobweb.gradle.library.tasks.KobwebGenerateMetadataBackendTask
import com.varabyte.kobweb.gradle.library.tasks.KobwebGenerateMetadataFrontendTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebLibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(KobwebCorePlugin::class.java)
        val kobwebBlock = project.kobwebBlock

        val kobwebGenFrontendMetadata =
            project.tasks.register(
                "kobwebGenFrontendMetadata",
                KobwebGenerateMetadataFrontendTask::class.java,
                kobwebBlock
            )

        val kobwebGenBackendMetadata =
            project.tasks.register(
                "kobwebGenBackendMetadata",
                KobwebGenerateMetadataBackendTask::class.java,
                kobwebBlock
            )

        project.afterEvaluate {
            project.tasks.findByName(jsTarget.processResources)?.dependsOn(kobwebGenFrontendMetadata)
            project.tasks.findByName(jsTarget.jar)?.dependsOn(kobwebGenFrontendMetadata)

            // NOTE: JVM-related tasks are not always available. If they are, it means this project exports an API jar.
            jvmTarget?.let { jvmTarget ->
                project.tasks.findByName(jvmTarget.processResources)?.dependsOn(kobwebGenBackendMetadata)
                project.tasks.findByName(jvmTarget.jar)?.dependsOn(kobwebGenBackendMetadata)
            }
        }
    }
}

fun Project.notifyKobwebAboutFrontendCodeGeneratingTask(task: Task) {
    tasks.named("kobwebGenFrontendMetadata") { dependsOn(task) }
}

fun Project.notifyKobwebAboutBackendCodeGeneratingTask(task: Task) {
    tasks.named("kobwebGenBackendMetadata") { dependsOn(task) }
}
