@file:Suppress("DEPRECATION") // Providing legacy support for deprecated `LibraryIndexMetadata`

package com.varabyte.kobweb.gradle.library

import com.varabyte.kobweb.ProcessorMode
import com.varabyte.kobweb.gradle.core.KobwebCorePlugin
import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.JvmTarget
import com.varabyte.kobweb.gradle.core.kmp.buildTargets
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobweb.gradle.core.ksp.applyKspPlugin
import com.varabyte.kobweb.gradle.core.ksp.setKspMode
import com.varabyte.kobweb.gradle.core.ksp.setupKspJs
import com.varabyte.kobweb.gradle.core.ksp.setupKspJvm
import com.varabyte.kobweb.gradle.core.util.generateModuleMetadataFor
import com.varabyte.kobweb.gradle.library.extensions.createLibraryBlock
import com.varabyte.kobweb.gradle.library.extensions.index
import com.varabyte.kobweb.gradle.library.extensions.library
import com.varabyte.kobweb.gradle.library.tasks.KobwebGenerateIndexMetadataTask
import com.varabyte.kobweb.gradle.library.tasks.KobwebGenerateLibraryMetadataTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebLibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(KobwebCorePlugin::class.java)
        project.kobwebBlock.createLibraryBlock()
        project.applyKspPlugin()
        project.setKspMode(ProcessorMode.LIBRARY)

        val kobwebGenerateIndexMetadataTask =
            project.tasks.register<KobwebGenerateIndexMetadataTask>("kobwebGenerateIndexMetadata")
        val kobwebGenerateLibraryMetadataTask = project.tasks
            .register<KobwebGenerateLibraryMetadataTask>("kobwebGenerateLibraryMetadata") {
                indexHead.set(project.kobwebBlock.library.index.serializedHead)
            }
        project.buildTargets.withType<KotlinJsIrTarget>().configureEach {
            val jsTarget = JsTarget(this)
            project.setupKspJs(jsTarget)
            project.generateModuleMetadataFor(jsTarget)
            project.kotlin.sourceSets.named(jsTarget.mainSourceSet) {
                resources.srcDir(kobwebGenerateLibraryMetadataTask)
                resources.srcDir(kobwebGenerateIndexMetadataTask) // Legacy task, kept around for a little while to give users a chance to upgrade
            }
        }

        project.buildTargets.withType<KotlinJvmTarget>().configureEach {
            val jvmTarget = JvmTarget(this)
            project.setupKspJvm(jvmTarget)
            project.generateModuleMetadataFor(jvmTarget)
        }
    }
}
