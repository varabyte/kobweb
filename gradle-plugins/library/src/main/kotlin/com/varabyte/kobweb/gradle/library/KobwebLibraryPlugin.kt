package com.varabyte.kobweb.gradle.library


import com.varabyte.kobweb.gradle.core.KobwebCorePlugin
import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.JvmTarget
import com.varabyte.kobweb.gradle.core.kmp.buildTargets
import com.varabyte.kobweb.gradle.core.ksp.setupKsp
import com.varabyte.kobweb.gradle.core.kspBackendFile
import com.varabyte.kobweb.gradle.core.kspFrontendFile
import com.varabyte.kobweb.gradle.core.util.namedOrNull
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebLibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(KobwebCorePlugin::class.java)
        val kobwebBlock = project.kobwebBlock

        setupKsp(project, kobwebBlock, includeAppData = false)

        project.buildTargets.withType<KotlinJsIrTarget>().configureEach {
            // TODO: why doesn't declaring the inputs work
            val kspFrontendFile = project.kspFrontendFile.map { it.asFile.absolutePath }
            val jsTarget = JsTarget(this)

            project.tasks.namedOrNull(jsTarget.processResources)?.configure {
                dependsOn(project.tasks.named("kspKotlinJs"))
//                inputs.file(kspFrontendFile)
            }
            project.tasks.namedOrNull(jsTarget.jar)?.configure {
                dependsOn(project.tasks.named("kspKotlinJs"))
//                inputs.file(kspFrontendFile)
            }
        }
        project.buildTargets.withType<KotlinJvmTarget>().configureEach {
            val kspBackendFile = project.kspBackendFile
            val jvmTarget = JvmTarget(this)

            // NOTE: JVM-related tasks are not always available. If they are, it means this project exports an API jar.
            project.tasks.namedOrNull(jvmTarget.processResources)?.configure {
                // TODO: are we doing something wrong or is this fine - (also in application)
                (this as Copy).duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//                dependsOn(project.tasks.named("kspKotlinJvm"))
                if (kspBackendFile != null) {
                    inputs.file(kspBackendFile)
                }
            }
            project.tasks.namedOrNull(jvmTarget.jar)?.configure {
//                dependsOn(project.tasks.named("kspKotlinJvm"))
                if (kspBackendFile != null) {
                    inputs.file(kspBackendFile)
                }
            }
        }
    }
}

fun Project.notifyKobwebAboutFrontendCodeGeneratingTask(task: Task) {
    // TODO: make ksp depend on the Task
//    tasks.named("kobwebGenFrontendMetadata") { dependsOn(task) }
}

fun Project.notifyKobwebAboutBackendCodeGeneratingTask(task: Task) {
    // TODO: make ksp depend on the Task
//    tasks.named("kobwebGenBackendMetadata") { dependsOn(task) }
}
