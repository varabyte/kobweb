package com.varabyte.kobweb.gradle.core

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobweb.gradle.core.kmp.sourceSets
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebCorePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // A `kobweb` block is not used directly here in the core plugin but is provided as a foundational building
        // block for both library and application plugins.
        val kobwebBlock = project.extensions.create("kobweb", KobwebBlock::class.java)

        // Kobweb applications and libraries both put stuff in generated folders, which the Kotlin project should be
        // aware of.
        project.afterEvaluate {
            kotlin {
                val genDir = kobwebBlock.genDir.get()
                sourceSets {
                    getByName(jsTarget.mainSourceSet) {
                        kotlin.srcDir(project.layout.buildDirectory.dir("$genDir${jsTarget.srcSuffix}"))
                        resources.srcDir(project.layout.buildDirectory.dir("$genDir${jsTarget.resourceSuffix}"))
                    }

                    jvmTarget?.let { jvm ->
                        getByName(jvm.mainSourceSet) {
                            kotlin.srcDir(project.layout.buildDirectory.dir("$genDir${jvm.srcSuffix}"))
                            resources.srcDir(project.layout.buildDirectory.dir("$genDir${jvm.resourceSuffix}"))
                        }
                    }
                }
            }
        }
    }
}