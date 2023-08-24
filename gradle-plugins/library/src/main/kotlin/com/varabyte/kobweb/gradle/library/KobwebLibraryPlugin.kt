package com.varabyte.kobweb.gradle.library

import com.varabyte.kobweb.gradle.core.KobwebCorePlugin
import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.JvmTarget
import com.varabyte.kobweb.gradle.core.kmp.buildTargets
import com.varabyte.kobweb.gradle.core.ksp.setupKsp
import com.varabyte.kobweb.gradle.core.util.namedOrNull
import com.varabyte.kobweb.gradle.library.tasks.KobwebGenerateMetadataBackendTask
import com.varabyte.kobweb.gradle.library.tasks.KobwebGenerateMetadataFrontendTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebLibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(KobwebCorePlugin::class.java)
        val kobwebBlock = project.kobwebBlock

        setupKsp(project, kobwebBlock)

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

        project.buildTargets.withType<KotlinJsIrTarget>().configureEach {
            val jsTarget = JsTarget(this)

            project.tasks.namedOrNull(jsTarget.processResources)?.configure { dependsOn(kobwebGenFrontendMetadata) }
            project.tasks.namedOrNull(jsTarget.jar)?.configure { dependsOn(kobwebGenFrontendMetadata) }
        }
        project.buildTargets.withType<KotlinJvmTarget>().configureEach {
            val jvmTarget = JvmTarget(this)

            // NOTE: JVM-related tasks are not always available. If they are, it means this project exports an API jar.
            project.tasks.namedOrNull(jvmTarget.processResources)?.configure { dependsOn(kobwebGenBackendMetadata) }
            project.tasks.namedOrNull(jvmTarget.jar)?.configure { dependsOn(kobwebGenBackendMetadata) }
        }
    }
}

fun Project.notifyKobwebAboutFrontendCodeGeneratingTask(task: Task) {
    tasks.named("kobwebGenFrontendMetadata") { dependsOn(task) }
}

fun Project.notifyKobwebAboutBackendCodeGeneratingTask(task: Task) {
    tasks.named("kobwebGenBackendMetadata") { dependsOn(task) }
}
