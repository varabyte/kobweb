package com.varabyte.kobweb.gradle.application

import com.varabyte.kobweb.gradle.application.buildservices.KobwebTaskListener
import com.varabyte.kobweb.gradle.application.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.application.extensions.KobwebxBlock
import com.varabyte.kobweb.gradle.application.extensions.hasDependencyNamed
import com.varabyte.kobweb.gradle.application.extensions.index
import com.varabyte.kobweb.gradle.application.kmp.jsTarget
import com.varabyte.kobweb.gradle.application.kmp.jvmTarget
import com.varabyte.kobweb.gradle.application.kmp.kotlin
import com.varabyte.kobweb.gradle.application.kmp.sourceSets
import com.varabyte.kobweb.gradle.application.tasks.KobwebExportTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebGenerateApiTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebGenerateSiteTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebStartTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebStopTask
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.conf.KobwebConfFile
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerRequest
import com.varabyte.kobweb.server.api.ServerRequestsFile
import com.varabyte.kobweb.server.api.ServerStateFile
import com.varabyte.kobweb.server.api.SiteLayout
import kotlinx.html.link
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.tooling.events.FailureResult
import javax.inject.Inject

val Project.kobwebFolder: KobwebFolder
    get() = KobwebFolder.fromChildPath(layout.projectDirectory.asFile.toPath())
        ?: throw GradleException("This project is not a Kobweb project but is applying the Kobweb plugin.")

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebApplicationPlugin @Inject constructor(
    private val buildEventsListenerRegistry: BuildEventsListenerRegistry
) : Plugin<Project> {
    override fun apply(project: Project) {
        val kobwebFolder = project.kobwebFolder
        val kobwebConf = KobwebConfFile(kobwebFolder).content ?: throw GradleException("Missing conf.yaml file from Kobweb folder")
        val kobwebBlock = project.extensions.create("kobweb", KobwebBlock::class.java, kobwebConf)
        project.extensions.create("kobwebx", KobwebxBlock::class.java)

        val env =
            project.findProperty("kobwebEnv")?.let { ServerEnvironment.valueOf(it.toString()) } ?: ServerEnvironment.DEV
        val runLayout =
            project.findProperty("kobwebRunLayout")?.let { SiteLayout.valueOf(it.toString()) } ?: SiteLayout.KOBWEB
        val exportLayout =
            project.findProperty("kobwebExportLayout")?.let { SiteLayout.valueOf(it.toString()) } ?: SiteLayout.KOBWEB
        val buildTarget = project.findProperty("kobwebBuildTarget")?.let { BuildTarget.valueOf(it.toString()) }
            ?: if (env == ServerEnvironment.DEV) BuildTarget.DEBUG else BuildTarget.RELEASE

        val kobwebGenSiteTask =
            project.tasks.register("kobwebGenSite", KobwebGenerateSiteTask::class.java, kobwebBlock, buildTarget)
        val kobwebGenApiTask = project.tasks.register("kobwebGenApi", KobwebGenerateApiTask::class.java, kobwebBlock)
        // Umbrella task for all other gen tasks
        val kobwebGenTask = project.tasks.register("kobwebGen")
        kobwebGenTask.configure {
            dependsOn(kobwebGenSiteTask)
            dependsOn(kobwebGenApiTask)
        }

        val kobwebStartTask = run {
            val reuseServer = project.findProperty("kobwebReuseServer")?.let { it.toString().toBoolean() } ?: true
            project.tasks.register("kobwebStart", KobwebStartTask::class.java, env, runLayout, reuseServer)
        }
        project.tasks.register("kobwebStop", KobwebStopTask::class.java)
        val kobwebExportTask =
            project.tasks.register("kobwebExport", KobwebExportTask::class.java, kobwebBlock, env, exportLayout)

        // Note: I'm pretty sure I'm abusing build service tasks by adding a listener to it directly but I'm not sure
        // how else I'm supposed to do this
        val taskListenerService = project.gradle.sharedServices.registerIfAbsent("kobweb-task-listener", KobwebTaskListener::class.java) {}
        run {
            var isBuilding = false
            var isServerRunning = run {
                val stateFile = ServerStateFile(kobwebFolder)
                stateFile.content?.let { serverState ->
                    ProcessHandle.of(serverState.pid).isPresent
                }
            } ?: false

            taskListenerService.get().onFinishCallbacks.add { event ->
                if (kobwebStartTask.name !in project.gradle.startParameter.taskNames) return@add

                val taskName = event.descriptor.name.substringAfterLast(":")
                val serverRequestsFile = ServerRequestsFile(kobwebFolder)
                val taskFailed = event.result is FailureResult

                if (isServerRunning) {
                    if (taskFailed) {
                        serverRequestsFile.enqueueRequest(
                            ServerRequest.SetStatus(
                                "Failed.",
                                isError = true,
                                timeoutMs = 500
                            )
                        )
                    } else {
                        if (taskName == kobwebStartTask.name) {
                            serverRequestsFile.enqueueRequest(ServerRequest.ClearStatus())
                            serverRequestsFile.enqueueRequest(ServerRequest.IncrementVersion())
                        } else if (!isBuilding) {
                            serverRequestsFile.enqueueRequest(ServerRequest.SetStatus("Building..."))
                            isBuilding = true
                        }
                    }
                } else {
                    if (!taskFailed && taskName == kobwebStartTask.name) {
                        isServerRunning = true
                    }
                }
            }
        }
        buildEventsListenerRegistry.onTaskCompletion(taskListenerService)

        project.afterEvaluate {
            project.tasks.named("clean") {
                doLast {
                    delete(kobwebConf.server.files.prod.siteRoot)
                    delete(kobwebFolder.resolve("server"))
                }
            }

            val jsRunTasks = listOf(
                jsTarget.browserDevelopmentRun, jsTarget.browserProductionRun,
                jsTarget.browserRun, jsTarget.run,
            )

            // Users should be using Kobweb commands instead of the standard Compose for Web commands, but they
            // probably don't know that. We do our best to work even in those cases, but warn the user to prefer
            // the Kobweb commands instead.
            jsRunTasks.forEach { taskName ->
                project.tasks.named(taskName) {
                    doFirst {
                        logger.error("With Kobweb, you should run `gradlew kobwebStart` instead. Some site behavior may not work.")
                    }
                }
            }

            if (project.hasDependencyNamed("kobweb-silk-icons-fa")) {
                kobwebBlock.index.head.add {
                    link {
                        rel = "stylesheet"
                        href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css"
                    }
                }
            }

            val cleanTask = project.tasks.named("clean")
            project.tasks.named(jsTarget.compileKotlin) {
                dependsOn(kobwebGenSiteTask)
            }
            project.tasks.named(jsTarget.processResources) {
                dependsOn(kobwebGenSiteTask)
            }

            // NOTE: JVM-related tasks are not always available. If so, it means this project exports an API jar.
            jvmTarget?.let { jvm ->
                project.tasks.findByName(jvm.compileKotlin)?.dependsOn(kobwebGenApiTask)

                project.tasks.findByName(jvm.jar)?.dependsOn(kobwebGenApiTask)
            }


            val compileExecutableTask = when (buildTarget) {
                BuildTarget.DEBUG -> project.tasks.named(jsTarget.developmentExecutableCompileSync)
                BuildTarget.RELEASE -> project.tasks.named(jsTarget.productionExecutableCompileSync)
            }
            kobwebStartTask.configure {
                jvmTarget?.let { jvm ->
                    dependsOn(project.tasks.findByName(jvm.jar))
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
                    getByName(jsTarget.mainSourceSet) {
                        kotlin.srcDir(project.layout.buildDirectory.dir("$GENERATED_ROOT${jsTarget.srcSuffix}"))
                        resources.srcDir(project.layout.buildDirectory.dir("$GENERATED_ROOT${jsTarget.resourceSuffix}"))
                    }

                    jvmTarget?.let { jvm ->
                        getByName(jvm.mainSourceSet) {
                            kotlin.srcDir(project.layout.buildDirectory.dir("$GENERATED_ROOT${jvm.srcSuffix}"))
                        }
                    }
                }
            }
        }
    }
}