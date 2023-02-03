package com.varabyte.kobweb.gradle.core

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobweb.gradle.core.kmp.sourceSets
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebCorePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // A `kobweb` block is not used directly here in the core plugin but is provided as a foundational building
        // block for both library and application plugins.
        val kobwebBlock = project.extensions.create("kobweb", KobwebBlock::class.java)

        // Starting in Kotlin 1.8.0, you can start getting errors around yarn locking. The experience sucks for
        // Kobweb users, since the binary tries to shield people from using Gradle directly when possible.
        // Here, we go ahead and make the rules about yarn locking a bit softer, but full disclosure, I don't
        // 100% know if my choices here have unexpected consequences.
        // The following code adapted from https://kotlinlang.org/docs/js-project-setup.html#reporting-that-yarn-lock-has-been-updated
        project.rootProject.plugins.withType(YarnPlugin::class.java) {
            with(project.rootProject.the<YarnRootExtension>()) {
                yarnLockMismatchReport = YarnLockMismatchReport.WARNING
                yarnLockAutoReplace = true
            }
        }

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