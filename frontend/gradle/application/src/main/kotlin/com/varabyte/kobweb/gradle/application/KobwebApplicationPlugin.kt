package com.varabyte.kobweb.gradle.application

import com.varabyte.kobweb.common.KobwebFolder
import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.extensions.KobwebxBlock
import com.varabyte.kobweb.gradle.application.kmp.kotlin
import com.varabyte.kobweb.gradle.application.kmp.sourceSets
import com.varabyte.kobweb.gradle.application.tasks.KobwebGenerateTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebStartTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebStopTask
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerRequest
import com.varabyte.kobweb.server.api.ServerRequestsFile
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting

val Project.kobwebFolder: KobwebFolder
    get() = KobwebFolder.fromChildPath(layout.projectDirectory.asFile.toPath())
        ?: throw GradleException("This project is not a Kobweb project but is applying the Kobweb plugin.")

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebApplicationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val kobwebFolder = project.kobwebFolder
        val kobwebConfig = project.extensions.create("kobweb", KobwebConfig::class.java)
        project.extensions.create("kobwebx", KobwebxBlock::class.java)

        val kobwebGenTask = project.tasks.register("kobwebGen", KobwebGenerateTask::class.java, kobwebConfig)
        val kobwebStartDevTask = project.tasks.register("kobwebStartDev", KobwebStartTask::class.java, ServerEnvironment.DEV)
        val kobwebStartProdTask = project.tasks.register("kobwebStartProd", KobwebStartTask::class.java, ServerEnvironment.PROD)
        project.tasks.register("kobwebStop", KobwebStopTask::class.java)

        project.afterEvaluate {
            project.tasks.named("compileKotlinJs") {
                dependsOn(kobwebGenTask)
            }
            project.tasks.named("jsProcessResources") {
                dependsOn(kobwebGenTask)
            }

            val compileDevExecutableTask = project.tasks.named("jsDevelopmentExecutableCompileSync")
            kobwebStartDevTask.configure {
                dependsOn(compileDevExecutableTask)
            }
            val compileProdExecutableTask = project.tasks.named("jsProductionExecutableCompileSync")
            kobwebStartProdTask.configure {
                dependsOn(compileProdExecutableTask)
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

            project.gradle.taskGraph.addTaskExecutionListener(object : TaskExecutionListener {
                override fun beforeExecute(task: Task) {
                    if (task.name == kobwebGenTask.name) {
                        ServerRequestsFile(kobwebFolder).enqueueRequest(ServerRequest.SetStatus("Rebuilding project"))
                    }
                }
                override fun afterExecute(task: Task, state: TaskState) {
                    if (task.name in listOf(compileDevExecutableTask, compileProdExecutableTask).map { it.name }) {
                        if (state.failure == null) {
                            ServerRequestsFile(kobwebFolder).enqueueRequest(ServerRequest.ClearStatus())
                            ServerRequestsFile(kobwebFolder).enqueueRequest(ServerRequest.IncrementVersion())
                        }
                    }

                    if (state.failure != null) {
                        // TODO: Update to "Build Failed" status message when timeout is supported
//                        ServerRequestsFile(kobwebFolder).enqueueRequest(ServerRequest.SetStatus("Build failed", 5000))
                        ServerRequestsFile(kobwebFolder).enqueueRequest(ServerRequest.ClearStatus())
                    }
                }
            })
        }
    }
}