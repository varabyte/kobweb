package com.varabyte.kobweb.gradle.library


import com.varabyte.kobweb.gradle.core.KobwebCorePlugin
import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.JvmTarget
import com.varabyte.kobweb.gradle.core.kmp.buildTargets
import com.varabyte.kobweb.gradle.core.ksp.setupKsp
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
            val jsTarget = JsTarget(this)
            listOf(jsTarget.processResources, jsTarget.jar).forEach { taskName ->
                project.tasks.namedOrNull(taskName)?.configure {
                    inputs.file(project.kspFrontendFile)
                }
            }
        }
        project.buildTargets.withType<KotlinJvmTarget>().configureEach {
            val jvmTarget = JvmTarget(this)
            // NOTE: JVM-related tasks are not always available. If they are, it means this project exports an API jar.
            listOf(jvmTarget.processResources, jvmTarget.jar).forEach { taskName ->
                project.tasks.namedOrNull(taskName)?.configure {
                    inputs.file(project.kspFrontendFile)
                }
            }

            // TODO: are we doing something wrong or is this fine - (also in application)
            project.tasks.namedOrNull(jvmTarget.processResources)?.configure {
                (this as Copy).duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
        }
    }
}

@Deprecated(
    "Add the task outputs to the source set directly instead. Note that you may have to adjust the task to output a directory instead of a file.",
    ReplaceWith("kotlin.sourceSets.getByName(\"jsMain\").kotlin.srcDir(task)"),
)
fun Project.notifyKobwebAboutFrontendCodeGeneratingTask(task: Task) {
    tasks.matching { it.name == "kspKotlinJs" }.configureEach { dependsOn(task) }
}

@Deprecated(
    "Add the task outputs to the source set directly instead. Note that you may have to adjust the task to output a directory instead of a file.",
    ReplaceWith("kotlin.sourceSets.getByName(\"jvmMain\").kotlin.srcDir(task)"),
)
fun Project.notifyKobwebAboutBackendCodeGeneratingTask(task: Task) {
    tasks.matching { it.name == "kspKotlinJvm" }.configureEach { dependsOn(task) }
}
