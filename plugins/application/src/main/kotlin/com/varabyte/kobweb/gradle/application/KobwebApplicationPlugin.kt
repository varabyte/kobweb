package com.varabyte.kobweb.gradle.application

import com.varabyte.kobweb.gradle.application.GENERATED_ROOT
import com.varabyte.kobweb.gradle.application.kmp.kotlin
import com.varabyte.kobweb.gradle.application.kmp.sourceSets
import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.tasks.KobwebGenerateTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting

@Suppress("unused")
class KobwebApplicationPlugin : Plugin<Project> {
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