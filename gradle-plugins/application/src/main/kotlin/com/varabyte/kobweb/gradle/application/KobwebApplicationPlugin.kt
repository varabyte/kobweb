package com.varabyte.kobweb.gradle.application

import com.varabyte.kobweb.common.path.toUnixSeparators
import com.varabyte.kobweb.gradle.application.buildservices.KobwebTaskListener
import com.varabyte.kobweb.gradle.application.extensions.createAppBlock
import com.varabyte.kobweb.gradle.application.tasks.*
import com.varabyte.kobweb.gradle.core.KobwebCorePlugin
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.conf.KobwebConfFile
import com.varabyte.kobweb.server.api.*
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionAware
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke
import org.gradle.tooling.events.FailureResult
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import javax.inject.Inject

val Project.kobwebFolder: KobwebFolder
    get() = KobwebFolder.fromChildPath(layout.projectDirectory.asFile.toPath())
        ?: throw GradleException("This project is not a Kobweb project but is applying the Kobweb plugin.")

private const val DISMISS_GRANULAIRTY_KEY = "kobweb.dismiss.granularity.warning"

private const val GRANULARITY_SETTING_KEY = "kotlin.js.ir.output.granularity"
private const val GRANULARITY_WHOLE_PROGRAM = "whole-program"

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

        val kobwebCopyDependencyResourcesTask = project.tasks.register("kobwebCopyDepResources", KobwebCopyDependencyResources::class.java, kobwebBlock)
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

        val kobwebStartTask = run {
            val reuseServer = project.findProperty("kobwebReuseServer")?.let { it.toString().toBoolean() } ?: true
            project.tasks.register("kobwebStart", KobwebStartTask::class.java, env, runLayout, reuseServer)
        }
        project.tasks.register("kobwebStop", KobwebStopTask::class.java)
        val kobwebExportTask =
            project.tasks.register("kobwebExport", KobwebExportTask::class.java, kobwebConf, kobwebBlock, exportLayout)

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

            val cleanTask = project.tasks.named("clean")
            cleanTask {
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
            jsRunTasks
                .mapNotNull { taskName -> project.tasks.findByName(taskName) }
                .forEach { task ->
                    task.doFirst {
                        logger.error("With Kobweb, you should run `gradlew kobwebStart` instead. Some site behavior may not work.")
                    }
                }

            kobwebGenTask.configure {
                dependsOn(kobwebGenFrontendTask)
                if (jvmTarget != null) {
                    dependsOn(kobwebGenBackendTask)
                }
            }

            project.tasks.named(jsTarget.compileKotlin) {
                dependsOn(kobwebGenSiteEntryTask)
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

            val webpackTask = when (buildTarget) {
                BuildTarget.DEBUG -> project.tasks.findByName(jsTarget.browserDevelopmentWebpack)
                BuildTarget.RELEASE -> project.tasks.findByName(jsTarget.browserProductionWebpack)
            } as KotlinWebpack
            kobwebStartTask.configure {
                // PROD env uses files copied over into a site folder by the export task, so it doesn't need to trigger
                // much.
                if (env == ServerEnvironment.DEV) {
                    // If this site has server routes, make sure we built the jar that our servers can load
                    jvmTarget?.let { jvm -> dependsOn(project.tasks.findByName(jvm.jar)) }

                    dependsOn(kobwebGenTask)
                    dependsOn(webpackTask)

                    // The following warning is for a dev value only, but `kobweb export` ends up in this path because
                    // it creates a dev server in order to generate and snapshot files. Therefore, we also have to check
                    // the build target to make sure if we should show the warning or not.
                    // TODO(#168): Remove warning before v1.0
                    if (buildTarget == BuildTarget.DEBUG) {
                        doLast {

                            // Note: WebpackTask has an "outputFile" field but for some reason it's stale...
                            // so here, just check the destination
                            val webpackOutputDir =
                                webpackTask.destinationDirectory.relativeTo(project.layout.projectDirectory.asFile)
                                    .toUnixSeparators()

                            if (project.properties[DISMISS_GRANULAIRTY_KEY] != "true" &&
                                kobwebConf.server.files.dev.script.substringBeforeLast('/') != webpackOutputDir &&
                                (project.properties[GRANULARITY_SETTING_KEY] != GRANULARITY_WHOLE_PROGRAM
                                    || project.extra[GRANULARITY_SETTING_KEY] != GRANULARITY_WHOLE_PROGRAM)
                            ) {
                                project.logger.warn(
                                    """

                                        ${"-".repeat(70)}
                                        w: ⚠️  Starting in 0.11.6, Kobweb no longer forces whole-program granularity.

                                        If you're seeing this warning, you might get a runtime error when running
                                        your site that looks like:

                                        🚨 Error loading module '...'. Its dependency '...' was not found. 🚨

                                        To stop seeing this warning, take one of the following actions:

                                        1) Update your .kobweb/conf.yaml, setting the dev script path to:
                                           script: "$webpackOutputDir/${jsTarget.kotlinTarget.moduleName}.js"
                                           # You will also need to stop and restart your kobweb server

                                        2) Add the following line to your gradle.properties file:
                                           ${GRANULARITY_SETTING_KEY}=${GRANULARITY_WHOLE_PROGRAM}

                                        3) Add the following line to your gradle.properties file:
                                           ${DISMISS_GRANULAIRTY_KEY}=true
                                        ${"-".repeat(70)}
                                    """.trimIndent()
                                )
                            }
                        }
                    }
                }
            }

            kobwebExportTask.configure {
                // Exporting ALWAYS spins up a dev server, so that way it loads the files it needs from dev locations
                // before outputting them into a final prod folder.
                check(env == ServerEnvironment.DEV)

                dependsOn(cleanTask)
                dependsOn(project.tasks.named(jsTarget.browserProductionWebpack))
                dependsOn(kobwebStartTask)
            }
        }
    }
}

fun Project.notifyKobwebAboutFrontendCodeGeneratingTask(task: Task) {
    tasks.named("kobwebGenFrontendMetadata") { dependsOn(task) }
}

fun Project.notifyKobwebAboutBackendCodeGeneratingTask(task: Task) {
    tasks.named("kobwebGenBackendMetadata") { dependsOn(task) }
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
