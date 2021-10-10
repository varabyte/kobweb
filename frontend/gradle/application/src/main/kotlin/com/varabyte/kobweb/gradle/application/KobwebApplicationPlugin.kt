package com.varabyte.kobweb.gradle.application

import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.extensions.KobwebxBlock
import com.varabyte.kobweb.gradle.application.kmp.kotlin
import com.varabyte.kobweb.gradle.application.kmp.sourceSets
import com.varabyte.kobweb.gradle.application.tasks.KobwebExportTask
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

        val env = project.findProperty("kobwebEnv")?.let { ServerEnvironment.valueOf(it.toString()) } ?: ServerEnvironment.DEV
        val buildTarget = project.findProperty("kobwebBuildTarget")?.let { BuildTarget.valueOf(it.toString()) }
            ?: if (env == ServerEnvironment.DEV) BuildTarget.DEBUG else BuildTarget.RELEASE
        val kobwebGenTask = project.tasks.register("kobwebGen", KobwebGenerateTask::class.java, kobwebConfig, buildTarget)

        val kobwebStartTask = run {
            val reuseServer = project.findProperty("kobwebReuseServer")?.let { it.toString().toBoolean() } ?: true
            project.tasks.register("kobwebStart", KobwebStartTask::class.java, env, reuseServer)
        }
        project.tasks.register("kobwebStop", KobwebStopTask::class.java)
        val kobwebExportTask = project.tasks.register("kobwebExport", KobwebExportTask::class.java, kobwebConfig)

        project.afterEvaluate {
            project.tasks.named("compileKotlinJs") {
                dependsOn(kobwebGenTask)
            }
            project.tasks.named("jsProcessResources") {
                dependsOn(kobwebGenTask)
            }

            val compileExecutableTask = when(buildTarget) {
                BuildTarget.DEBUG -> project.tasks.named("jsDevelopmentExecutableCompileSync")
                BuildTarget.RELEASE -> project.tasks.named("jsProductionExecutableCompileSync")
            }
            kobwebStartTask.configure {
                dependsOn(compileExecutableTask)
            }
            kobwebExportTask.configure {
                dependsOn(kobwebStartTask)
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
                    if (task.name == compileExecutableTask.name) {
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