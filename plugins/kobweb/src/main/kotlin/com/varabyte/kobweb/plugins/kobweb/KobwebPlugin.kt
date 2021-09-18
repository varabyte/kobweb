package com.varabyte.kobweb.plugins.kobweb

import com.varabyte.kobweb.plugins.kobweb.kmp.kotlin
import com.varabyte.kobweb.plugins.kobweb.kmp.sourceSets
import com.varabyte.kobweb.plugins.kobweb.tasks.KobwebGenerateTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import java.io.File

@Suppress("unused")
class KobwebPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("kobwebGen", KobwebGenerateTask::class.java) {
            configFile.set(File(project.projectDir, "kobweb.conf.yaml"))
            genDir.set(File(project.projectDir, GENERATED_ROOT))
        }

        project.afterEvaluate {
            project.kotlin {
                sourceSets {
                    @Suppress("UNUSED_VARIABLE") // jsMain name is necessary for "getting"
                    val jsMain by getting {
                        kotlin.srcDir("$GENERATED_ROOT$SRC_SUFFIX")
                        resources.srcDir("$GENERATED_ROOT$RESOURCE_SUFFIX")
                    }
                }
            }
        }
    }
}