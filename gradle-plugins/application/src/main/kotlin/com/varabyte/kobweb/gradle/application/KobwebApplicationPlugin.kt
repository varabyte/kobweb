package com.varabyte.kobweb.gradle.application

import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.extensions.KobwebxBlock
import com.varabyte.kobweb.gradle.application.extensions.index
import com.varabyte.kobweb.gradle.application.kmp.kotlin
import com.varabyte.kobweb.gradle.application.kmp.sourceSets
import com.varabyte.kobweb.gradle.application.tasks.KobwebExportTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebGenerateApiTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebGenerateSiteTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebStartTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebStopTask
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerRequest
import com.varabyte.kobweb.server.api.ServerRequestsFile
import kotlinx.html.link
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

        val env =
            project.findProperty("kobwebEnv")?.let { ServerEnvironment.valueOf(it.toString()) } ?: ServerEnvironment.DEV
        val buildTarget = project.findProperty("kobwebBuildTarget")?.let { BuildTarget.valueOf(it.toString()) }
            ?: if (env == ServerEnvironment.DEV) BuildTarget.DEBUG else BuildTarget.RELEASE

        val kobwebGenSiteTask =
            project.tasks.register("kobwebGenSite", KobwebGenerateSiteTask::class.java, kobwebConfig, buildTarget)
        val kobwebGenApiTask = project.tasks.register("kobwebGenApi", KobwebGenerateApiTask::class.java, kobwebConfig)
        // Umbrella task for all other gen tasks
        val kobwebGenTask = project.tasks.register("kobwebGen")
        kobwebGenTask.configure {
            dependsOn(kobwebGenSiteTask)
            dependsOn(kobwebGenApiTask)
        }

        val kobwebStartTask = run {
            val reuseServer = project.findProperty("kobwebReuseServer")?.let { it.toString().toBoolean() } ?: true
            project.tasks.register("kobwebStart", KobwebStartTask::class.java, env, reuseServer)
        }
        project.tasks.register("kobwebStop", KobwebStopTask::class.java)
        val kobwebExportTask = project.tasks.register("kobwebExport", KobwebExportTask::class.java, kobwebConfig)

        project.afterEvaluate {
            project.tasks.named("clean") {
                doLast {
                    delete(kobwebFolder.resolve("site"))
                    delete(kobwebFolder.resolve("server"))
                }
            }

            project.configurations.asSequence()
                .flatMap { config -> config.dependencies }
                .any { dependency -> dependency.name == "kobweb-silk-icons-fa" }
                .let { dependsOnSilkIcons ->
                    if (dependsOnSilkIcons) {
                        kobwebConfig.index.head.add {
                            link {
                                rel = "stylesheet"
                                href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
                            }
                        }
                    }
                }

            val cleanTask = project.tasks.named("clean")
            project.tasks.named("compileKotlinJs") {
                dependsOn(kobwebGenSiteTask)
            }
            project.tasks.named("jsProcessResources") {
                dependsOn(kobwebGenSiteTask)
            }

            // NOTE: JVM-related tasks are not always available. If so, it means this project exports an API jar.
            project.tasks.findByName("compileKotlinJvm")?.dependsOn(kobwebGenApiTask)

            val jarTask = project.tasks.findByName("jvmJar")
            jarTask?.dependsOn(kobwebGenApiTask)

            val compileExecutableTask = when (buildTarget) {
                BuildTarget.DEBUG -> project.tasks.named("jsDevelopmentExecutableCompileSync")
                BuildTarget.RELEASE -> project.tasks.named("jsProductionExecutableCompileSync")
            }
            kobwebStartTask.configure {
                if (jarTask != null) {
                    dependsOn(jarTask)
                }
                if (env == ServerEnvironment.DEV) {
                    dependsOn(compileExecutableTask)
                }
            }
            kobwebExportTask.configure {
                dependsOn(cleanTask)
                dependsOn(kobwebStartTask)
            }

            project.kotlin {
                sourceSets {
                    @Suppress("UNUSED_VARIABLE") // jsMain name is necessary for "getting"
                    val jsMain by getting {
                        kotlin.srcDir(project.layout.buildDirectory.dir("$GENERATED_ROOT$JS_SRC_SUFFIX"))
                        resources.srcDir(project.layout.buildDirectory.dir("$GENERATED_ROOT$JS_RESOURCE_SUFFIX"))
                    }

                    if (jarTask != null) {
                        @Suppress("UNUSED_VARIABLE") // jvmMain name is necessary for "getting"
                        val jvmMain by getting {
                            kotlin.srcDir(project.layout.buildDirectory.dir("$GENERATED_ROOT$JVM_SRC_SUFFIX"))
                        }
                    }
                }
            }

            project.gradle.taskGraph.addTaskExecutionListener(object : TaskExecutionListener {
                override fun beforeExecute(task: Task) {
                    if (task.name in listOf(kobwebGenSiteTask, kobwebGenApiTask).map { it.name }) {
                        ServerRequestsFile(kobwebFolder).enqueueRequest(ServerRequest.SetStatus("Building..."))
                    }

                    // Users should be using Kobweb commands instead of the standard Web Compose commands, but they
                    // probably don't know that. We do our best to work even in those cases, but warn the user to prefer
                    // the Kobweb commands instead.
                    if (task.name in listOf("jsBrowserDevelopmentRun", "jsBrowserProductionRun")) {
                        logger.error("With Kobweb, you should run `gradlew kobwebStart` instead. Some site behavior may not work.")
                    }
                }

                override fun afterExecute(task: Task, state: TaskState) {
                    if (task.name == kobwebStartTask.name) {
                        if (state.failure == null) {
                            ServerRequestsFile(kobwebFolder).enqueueRequest(ServerRequest.ClearStatus())
                            ServerRequestsFile(kobwebFolder).enqueueRequest(ServerRequest.IncrementVersion())
                        }
                    }

                    if (state.failure != null) {
                        ServerRequestsFile(kobwebFolder).enqueueRequest(
                            ServerRequest.SetStatus(
                                "Failed.",
                                isError = true,
                                500
                            )
                        )
                    }
                }
            })
        }
    }
}