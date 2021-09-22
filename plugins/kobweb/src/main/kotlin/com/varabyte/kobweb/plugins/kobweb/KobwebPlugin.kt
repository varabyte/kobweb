package com.varabyte.kobweb.plugins.kobweb

import com.varabyte.kobweb.plugins.kobweb.kmp.kotlin
import com.varabyte.kobweb.plugins.kobweb.kmp.sourceSets
import com.varabyte.kobweb.plugins.kobweb.extensions.KobwebConfig
import com.varabyte.kobweb.plugins.kobweb.tasks.KobwebGenerateTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting

@Suppress("unused")
class KobwebPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val kobwebConfig = project.extensions.create("kobweb", KobwebConfig::class.java)
        val kobwebGenTask = project.tasks.register("kobwebGen", KobwebGenerateTask::class.java, kobwebConfig)

        project.afterEvaluate {
            project.tasks.named("compileKotlinJs") {
                dependsOn(kobwebGenTask)
            }
            project.tasks.named("jsProcessResources") {
                dependsOn(kobwebGenTask)
            }

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