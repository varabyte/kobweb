package com.varabyte.kobweb.gradle.application

import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.kmp.kotlin
import com.varabyte.kobweb.gradle.application.kmp.sourceSets
import com.varabyte.kobweb.gradle.application.tasks.KobwebGenerateTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebStartTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebStopTask
import com.varabyte.kobweb.server.api.ServerEnvironment
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebApplicationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val kobwebConfig = project.extensions.create("kobweb", KobwebConfig::class.java)
        val kobwebGenTask = project.tasks.register("kobwebGen", KobwebGenerateTask::class.java, kobwebConfig)
        project.tasks.register("kobwebStartDev", KobwebStartTask::class.java, ServerEnvironment.DEV)
        project.tasks.register("kobwebStartProd", KobwebStartTask::class.java, ServerEnvironment.PROD)
        val kobwebStopTask = project.tasks.register("kobwebStop", KobwebStopTask::class.java)

        project.afterEvaluate {
            val sourcesTask = project.tasks.named("compileKotlinJs") {
                dependsOn(kobwebGenTask)
            }
            val resourcesTask = project.tasks.named("jsProcessResources") {
                dependsOn(kobwebGenTask)
            }
            project.tasks.withType(KobwebStartTask::class.java) {
                dependsOn(kobwebStopTask)
                dependsOn(sourcesTask)
                dependsOn(resourcesTask)
            }

            project.kotlin {
                sourceSets {
                    @Suppress("UNUSED_VARIABLE") // jsMain name is necessary for "getting"
                    val jsMain by getting {
                        kotlin.srcDir(project.layout.buildDirectory.dir("$GENERATED_ROOT$SRC_SUFFIX"))
                        resources.srcDir(project.layout.buildDirectory.dir("$GENERATED_ROOT$RESOURCE_SUFFIX"))
                    }
                }
            }
        }
    }
}