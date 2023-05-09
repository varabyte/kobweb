package com.varabyte.kobweb.gradle.application

import com.varabyte.kobweb.gradle.application.buildservices.KobwebTaskListener
import com.varabyte.kobweb.gradle.application.extensions.createAppBlock
import com.varabyte.kobweb.gradle.application.extensions.createExportBlock
import com.varabyte.kobweb.gradle.application.tasks.*
import com.varabyte.kobweb.gradle.core.KobwebCorePlugin
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.conf.KobwebConfFile
import com.varabyte.kobweb.server.api.*
import org.gradle.api.*
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.TaskProvider
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.tooling.events.FailureResult
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import javax.inject.Inject

val Project.kobwebFolder: KobwebFolder
    get() = KobwebFolder.fromChildPath(layout.projectDirectory.asFile.toPath())
        ?: throw GradleException("This project is not a Kobweb project but is applying the Kobweb plugin.")

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebApplicationPlugin @Inject constructor(
    private val buildEventsListenerRegistry: BuildEventsListenerRegistry
) : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(KobwebCorePlugin::class.java)

        val kobwebFolder = project.kobwebFolder
        val kobwebConf = KobwebConfFile(kobwebFolder).content ?: throw GradleException("Missing conf.yaml file from Kobweb folder")
        val kobwebBlock = ((project as ExtensionAware).extensions["kobweb"] as KobwebBlock).apply {
            createAppBlock(kobwebConf)
            createExportBlock()
        }

        val env =
            project.findProperty("kobwebEnv")?.let { ServerEnvironment.valueOf(it.toString()) } ?: ServerEnvironment.DEV
        val runLayout =
            project.findProperty("kobwebRunLayout")?.let { SiteLayout.valueOf(it.toString()) } ?: SiteLayout.KOBWEB
        val exportLayout =
            project.findProperty("kobwebExportLayout")?.let { SiteLayout.valueOf(it.toString()) } ?: SiteLayout.KOBWEB

        project.extra["kobwebBuildTarget"] = project.findProperty("kobwebBuildTarget")?.let { BuildTarget.valueOf(it.toString()) }
            ?: if (env == ServerEnvironment.DEV) BuildTarget.DEBUG else BuildTarget.RELEASE
        val buildTarget = project.kobwebBuildTarget

        val kobwebGenFrontendMetadata =
            project.tasks.register("kobwebGenFrontendMetadata", KobwebGenerateMetadataFrontendTask::class.java, kobwebBlock)

        val kobwebGenBackendMetadata =
            project.tasks.register("kobwebGenBackendMetadata", KobwebGenerateMetadataBackendTask::class.java, kobwebBlock)

        val kobwebGenSiteEntryTask =
            project.tasks.register("kobwebGenSiteEntry", KobwebGenerateSiteEntryTask::class.java, kobwebConf, kobwebBlock, buildTarget)
        kobwebGenSiteEntryTask.configure {
            dependsOn(kobwebGenFrontendMetadata)
        }

        val kobwebCopyDependencyResourcesTask = project.tasks.register("kobwebCopyDepResources", KobwebCopyDependencyResourcesTask::class.java, kobwebBlock)
        val kobwebGenSiteIndexTask =
            project.tasks.register("kobwebGenSiteIndex", KobwebGenerateSiteIndexTask::class.java, kobwebConf, kobwebBlock, buildTarget)

        kobwebGenSiteIndexTask.configure {
            // Make sure copy resources occurs first, so that our index.html file doesn't get overwritten
            dependsOn(kobwebCopyDependencyResourcesTask)
        }

        val kobwebGenApisFactoryTask = project.tasks.register("kobwebGenApisFactory", KobwebGenerateApisFactoryTask::class.java, kobwebBlock)
        kobwebGenApisFactoryTask.configure {
            dependsOn(kobwebGenBackendMetadata)
        }

        // Umbrella tasks for all other gen tasks
        val kobwebGenFrontendTask = project.tasks.register("kobwebGenFrontend", KobwebTask::class.java, "The umbrella task that combines all Kobweb frontend generation tasks")
        kobwebGenFrontendTask.configure {
            dependsOn(kobwebGenSiteIndexTask)
            dependsOn(kobwebGenSiteEntryTask)
        }
        val kobwebGenBackendTask = project.tasks.register("kobwebGenBackend", KobwebTask::class.java, "The umbrella task that combines all Kobweb backend generation tasks")
        kobwebGenBackendTask.configure {
            dependsOn(kobwebGenApisFactoryTask)
        }
        val kobwebGenTask = project.tasks.register("kobwebGen", KobwebTask::class.java, "The umbrella task that combines all frontend and backend Kobweb generation tasks")
        // Note: Configured below, in `afterEvaluate`

        val kobwebUnpackServerJarTask = project.tasks.register("kobwebUnpackServerJar", KobwebUnpackServerJarTask::class.java)
        val kobwebCreateServerScriptsTask = project.tasks.register("kobwebCreateServerScripts", KobwebCreateServerScriptsTask::class.java)
        val kobwebStartTask = run {
            val reuseServer = project.findProperty("kobwebReuseServer")?.let { it.toString().toBoolean() } ?: true
            project.tasks.register("kobwebStart", KobwebStartTask::class.java, env, runLayout, reuseServer)
        }
        kobwebStartTask.configure {
            dependsOn(kobwebUnpackServerJarTask)
        }
        project.tasks.register("kobwebStop", KobwebStopTask::class.java)

        val kobwebCleanSiteTask = project.tasks.register("kobwebCleanSite", KobwebTask::class.java, "Cleans all site artifacts generated by a previous export")
        kobwebCleanSiteTask.configure {
            doLast {
                project.delete(kobwebConf.server.files.prod.siteRoot)
            }
        }
        val kobwebCleanFolder = project.tasks.register("kobwebCleanFolder", KobwebTask::class.java, "Cleans all transient files that live in the .kobweb folder")
        kobwebCleanFolder.configure {
            dependsOn(kobwebCleanSiteTask)
            doLast {
                project.delete(kobwebFolder.resolve("server"))
            }
        }
        val kobwebExportTask =
            project.tasks.register("kobwebExport", KobwebExportTask::class.java, kobwebConf, kobwebBlock, exportLayout)

        project.tasks.register("kobwebBrowserCacheId", KobwebBrowserCacheIdTask::class.java, kobwebBlock)

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
            hackWorkaroundSinceWebpackTaskIsBrokenInContinuousMode()

            val jsRunTasks = listOf(
                jsTarget.browserDevelopmentRun, jsTarget.browserProductionRun,
                jsTarget.browserRun, jsTarget.run,
            )

            // Users should be using Kobweb tasks instead of the standard multiplatform tasks, but they
            // probably don't know that. We do our best to work even in those cases, but warn the user to prefer
            // the Kobweb commands instead.
            jsRunTasks
                .mapNotNull { taskName -> project.tasks.findByName(taskName) }
                .forEach { task ->
                    task.doFirst {
                        logger.error("With Kobweb, you should run `gradlew kobwebStart` instead. Some site behavior may not work.")
                    }
                }

            val jsSourceTasks = listOf(jsTarget.compileKotlin, jsTarget.sourcesJar)
            jsSourceTasks
                .mapNotNull { taskName -> project.tasks.findByName(taskName) }
                .forEach { task ->
                    task.dependsOn(kobwebGenSiteEntryTask)
                }

            kobwebGenTask.configure {
                dependsOn(kobwebGenFrontendTask)
                if (jvmTarget != null) {
                    dependsOn(kobwebGenBackendTask)
                }
            }

            project.tasks.named(jsTarget.processResources) {
                dependsOn(kobwebGenSiteIndexTask)
                dependsOn(kobwebCopyDependencyResourcesTask)
            }

            // NOTE: JVM-related tasks are not always available. If so, it means this project exports an API jar.
            jvmTarget?.let { jvm ->
                project.tasks.findByName(jvm.compileKotlin)?.dependsOn(kobwebGenBackendTask)
                project.tasks.findByName(jvm.jar)?.dependsOn(kobwebGenBackendTask)
            }

            // When exporting, both dev + production webpack actions are triggered - dev for the temporary server
            // that runs, and production for generating the final JS file for the site. The following order declarations
            // prevent Gradle from getting confused when both tasks are run at the same time.
            run {
                project.tasks.named(jsTarget.browserProductionWebpack) {
                    mustRunAfter(kobwebStartTask)
                }
                project.tasks.named(jsTarget.compileProductionExecutableKotlin) {
                    mustRunAfter(kobwebStartTask)
                }
            }

            kobwebStartTask.configure {
                // PROD env uses files copied over into a site folder by the export task, so it doesn't need to trigger
                // much.
                if (env == ServerEnvironment.DEV) {
                    // If this site has server routes, make sure we built the jar that our servers can load
                    jvmTarget?.let { jvm -> dependsOn(project.tasks.findByName(jvm.jar)) }

                    dependsOn(kobwebGenTask)
                    val webpackTask = project.tasks.findByName(jsTarget.browserDevelopmentWebpack) as KotlinWebpack
                    dependsOn(webpackTask)
                }
            }

            kobwebExportTask.configure {
                // Exporting ALWAYS spins up a dev server, so that way it loads the files it needs from dev locations
                // before outputting them into a final prod folder.
                check(env == ServerEnvironment.DEV)

                dependsOn(kobwebCleanSiteTask)
                dependsOn(kobwebCreateServerScriptsTask)
                dependsOn(kobwebStartTask)
                dependsOn(project.tasks.findByName(jsTarget.browserProductionWebpack))
            }
        }
    }
}

private fun Project.kobwebGenFrontendMetadata(action: Action<Task>) = tasks.named("kobwebGenFrontendMetadata", action)
private fun Project.kobwebGenBackendMetadata(action: Action<Task>) = tasks.named("kobwebGenBackendMetadata", action)

/**
 * Method provided for users to call if they generate their own Gradle task that generates some JS (frontend) code.
 *
 * Calling this ensures that their task will be triggered before the relevant Kobweb compilation task.
 */
fun Project.notifyKobwebAboutFrontendCodeGeneratingTask(task: Task) {
    kobwebGenFrontendMetadata { dependsOn(task) }
}

fun Project.notifyKobwebAboutFrontendCodeGeneratingTask(task: TaskProvider<*>) {
    kobwebGenFrontendMetadata { dependsOn(task) }
}

/**
 * Method provided for users to call if they generate their own Gradle task that generates some JVM (server) code.
 *
 * Calling this ensures that their task will be triggered before the relevant Kobweb compilation task.
 */
fun Project.notifyKobwebAboutBackendCodeGeneratingTask(task: Task) {
    kobwebGenBackendMetadata { dependsOn(task) }
}

fun Project.notifyKobwebAboutBackendCodeGeneratingTask(task: TaskProvider<*>) {
    kobwebGenBackendMetadata { dependsOn(task) }
}

val Project.kobwebBuildTarget get() = project.extra["kobwebBuildTarget"] as BuildTarget

// For context, see: https://youtrack.jetbrains.com/issue/KT-55820/jsBrowserDevelopmentWebpack-in-continuous-mode-doesnt-keep-outputs-up-to-date
// It seems like the webpack tasks are broken when run in continuous mode (it has a special branch of logic for handling
// `isContinuous` mode and I guess it just needs more time to bake).
// Unfortunately, `kobweb run` lives and dies on its live reloading behavior. So in order to allow it to support
// webpack, we need to get a little dirty here, using reflection to basically force the webpack task to always take the
// non-continuous logic branch.
// Basically, we're setting this value to always be false:
// https://github.com/JetBrains/kotlin/blob/4af0f110c7053d753c92fd9caafb4be138fdafba/libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/targets/js/webpack/KotlinWebpack.kt#L276
private fun Project.hackWorkaroundSinceWebpackTaskIsBrokenInContinuousMode() {
    tasks.withType(KotlinWebpack::class.java).forEach { webpackTask ->
        // Gradle generates subclasses via bytecode generation magic. Here, we need to grab the superclass to find
        // the private field we want.
        webpackTask::class.java.superclass.declaredFields
            // Note: Isn't ever null for now but checking protects us against future changes to KotlinWebpack
            .firstOrNull { it.name == "isContinuous" }
            ?.let { isContinuousField ->
                isContinuousField.isAccessible = true
                isContinuousField.setBoolean(webpackTask, false)
            }
    }
}
