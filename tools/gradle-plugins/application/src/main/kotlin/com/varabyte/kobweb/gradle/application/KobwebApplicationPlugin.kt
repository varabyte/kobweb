package com.varabyte.kobweb.gradle.application

import com.varabyte.kobweb.ProcessorMode
import com.varabyte.kobweb.gradle.application.buildservices.KobwebTaskListener
import com.varabyte.kobweb.gradle.application.extensions.app
import com.varabyte.kobweb.gradle.application.extensions.createAppBlock
import com.varabyte.kobweb.gradle.application.extensions.export
import com.varabyte.kobweb.gradle.application.ksp.kspBackendFile
import com.varabyte.kobweb.gradle.application.ksp.kspFrontendFile
import com.varabyte.kobweb.gradle.application.tasks.KobwebBrowserCacheIdTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebCopySupplementalResourcesTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebCopyWorkerJsOutputTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebCreateServerScriptsTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebExportTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebGenerateApisFactoryTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebGenerateSiteEntryTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebGenerateSiteIndexTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebGenerateTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebStartTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebStopTask
import com.varabyte.kobweb.gradle.application.tasks.KobwebUnpackServerJarTask
import com.varabyte.kobweb.gradle.application.util.kebabCaseToTitleCamelCase
import com.varabyte.kobweb.gradle.core.KobwebCorePlugin
import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.JvmTarget
import com.varabyte.kobweb.gradle.core.kmp.buildTargets
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobweb.gradle.core.ksp.applyKspPlugin
import com.varabyte.kobweb.gradle.core.ksp.setupKspJs
import com.varabyte.kobweb.gradle.core.ksp.setupKspJvm
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.core.util.configureHackWorkaroundSinceWebpackTaskIsBrokenInContinuousMode
import com.varabyte.kobweb.gradle.core.util.namedOrNull
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.conf.KobwebConfFile
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.ServerRequest
import com.varabyte.kobweb.server.api.ServerRequestsFile
import com.varabyte.kobweb.server.api.ServerStateFile
import com.varabyte.kobweb.server.api.SiteLayout
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.tooling.events.FailureResult
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import javax.inject.Inject
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

val Project.kobwebFolder: KobwebFolder
    get() = KobwebFolder.fromChildPath(layout.projectDirectory.asFile.toPath())
        ?: throw GradleException("This project is not a Kobweb project but is applying the Kobweb plugin.")

internal const val KOBWEB_SERVER_PLUGIN_CONFIGURATION_NAME = "kobwebServerPlugin"

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebApplicationPlugin @Inject constructor(
    private val buildEventsListenerRegistry: BuildEventsListenerRegistry
) : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(KobwebCorePlugin::class.java)
        project.applyKspPlugin()

        // A Kobweb Server Plugin is one which is loaded by the Kobweb server when it starts up. It's a way for users to
        // configure their ktor server in ways that Kobweb does not currently expose.
        project.configurations.register(KOBWEB_SERVER_PLUGIN_CONFIGURATION_NAME) {
            isTransitive = false
        }

        val kobwebFolder = project.kobwebFolder
        val kobwebConf = with(KobwebConfFile(kobwebFolder)) {
            // TODO(#310): Remove this hack before Kobweb v1.0. At that point, users are very likely to have picked up
            //  this update by then (or created a new app, where templates will have been updated).
            if (!path.exists()) {
                throw GradleException("Missing conf.yaml file from Kobweb folder")
            }

            val originalText = path.readText()
            val updatedText = originalText
                .replace("build/developmentExecutable", "build/dist/js/developmentExecutable")
                .replace("build/distributions", "build/dist/js/productionExecutable")

            if (originalText != updatedText) {
                project.logger.warn(
                    "The Compose HTML team changed the location of some output files in 1.5.1. In order to keep you working, we have automatically updated your `.kobweb/conf.yaml` file to reflect these changes."
                )
                path.writeText(updatedText)
            }
            content!!
        }

        val kobwebBlock = project.kobwebBlock.apply {
            createAppBlock(kobwebFolder, kobwebConf)
        }

        val env =
            project.findProperty("kobwebEnv")?.let { ServerEnvironment.valueOf(it.toString()) } ?: ServerEnvironment.DEV
        val runLayout =
            project.findProperty("kobwebRunLayout")?.let { SiteLayout.valueOf(it.toString()) } ?: SiteLayout.KOBWEB
        val exportLayout =
            project.findProperty("kobwebExportLayout")?.let { SiteLayout.valueOf(it.toString()) } ?: SiteLayout.KOBWEB

        project.extra["kobwebBuildTarget"] =
            project.findProperty("kobwebBuildTarget")?.let { BuildTarget.valueOf(it.toString()) }
                ?: if (env == ServerEnvironment.DEV) BuildTarget.DEBUG else BuildTarget.RELEASE
        val buildTarget = project.kobwebBuildTarget

        val kobwebGenSiteIndexTask = project.tasks
            .register<KobwebGenerateSiteIndexTask>("kobwebGenSiteIndex", kobwebConf, kobwebBlock, buildTarget)
        val kobwebCopySupplementalResourcesTask = project.tasks.register<KobwebCopySupplementalResourcesTask>(
            "kobwebCopySupplementalResources",
            kobwebBlock,
            kobwebGenSiteIndexTask.map { RegularFile { it.outputs.files.singleFile } }
        )
        val kobwebCopyWorkerJsOutputTask =
            project.tasks.register<KobwebCopyWorkerJsOutputTask>("kobwebCopyWorkerOutputJs", kobwebBlock)

        val kobwebUnpackServerJarTask = project.tasks.register<KobwebUnpackServerJarTask>("kobwebUnpackServerJar")
        val kobwebCreateServerScriptsTask = project.tasks
            .register<KobwebCreateServerScriptsTask>("kobwebCreateServerScripts")
        val kobwebStartTask = run {
            val reuseServer = project.findProperty("kobwebReuseServer")?.let { it.toString().toBoolean() } ?: true
            project.tasks.register<KobwebStartTask>("kobwebStart", kobwebBlock, env, runLayout, reuseServer)
        }

        val kobwebSyncServerPluginJarsTask = project.tasks.register<Sync>("kobwebSyncServerPluginJars") {
            group = "kobweb"
            description = "Copy all Kobweb server plugin jars (if any) into the server's plugins directory"

            from(project.configurations.named(KOBWEB_SERVER_PLUGIN_CONFIGURATION_NAME))
            into(project.projectDir.resolve(".kobweb/server/plugins"))
        }

        kobwebStartTask.configure {
            serverJar = kobwebUnpackServerJarTask.map { it.getServerJar() }
            serverPluginsDir = kobwebSyncServerPluginJarsTask.map { it.destinationDir }
            doLast {
                val devScript = kobwebConf.server.files.dev.script
                if (env == ServerEnvironment.DEV && !project.file(devScript).exists()) {
                    throw GradleException(
                        "e: Your .kobweb/conf.yaml dev script (\"$devScript\") could not be found. This will cause " +
                            "the page to fail to load with a 500 error. Perhaps search your build/ directory for " +
                            "\"${devScript.substringAfterLast('/')}\" to find the right path."
                    )
                }
            }
        }
        project.tasks.register<KobwebStopTask>("kobwebStop")

        val kobwebCleanSiteTask = project.tasks.register<KobwebTask>(
            "kobwebCleanSite",
            "Cleans all site artifacts generated by a previous export"
        )
        kobwebCleanSiteTask.configure {
            doLast {
                kobwebBlock.app.export.traceConfig.orNull?.let { traceConfig -> project.delete(traceConfig.root) }
                project.delete(kobwebConf.server.files.prod.siteRoot)
            }
        }
        val kobwebCleanFolderTask = project.tasks.register<KobwebTask>(
            "kobwebCleanFolder",
            "Cleans all transient files that live in the .kobweb folder"
        )
        kobwebCleanFolderTask.configure {
            dependsOn(kobwebCleanSiteTask)
            doLast {
                project.delete(kobwebFolder.resolve("server"))
            }
        }
        val kobwebExportTask = project.tasks
            .register<KobwebExportTask>("kobwebExport", kobwebConf, kobwebBlock, exportLayout)

        project.tasks.register<KobwebBrowserCacheIdTask>("kobwebBrowserCacheId", kobwebBlock)

        // Note: I'm pretty sure I'm abusing build service tasks by adding a listener to it directly but I'm not sure
        // how else I'm supposed to do this
        val taskListenerService = project.gradle.sharedServices
            .registerIfAbsent("kobweb-task-listener", KobwebTaskListener::class.java) {}
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

        project.tasks.withType<KotlinWebpack>().configureHackWorkaroundSinceWebpackTaskIsBrokenInContinuousMode()
        project.buildTargets.withType<KotlinJsIrTarget>().configureEach {
            val jsTarget = JsTarget(this)

            project.setupKspJs(jsTarget, ProcessorMode.APP)

            val kobwebGenSiteEntryTask = project.tasks
                .register<KobwebGenerateSiteEntryTask>("kobwebGenSiteEntry", kobwebConf, kobwebBlock, buildTarget)

            kobwebGenSiteEntryTask.configure {
                kspGenFile.set(project.kspFrontendFile(jsTarget))
            }

            val jsRunTasks = listOf(
                jsTarget.browserDevelopmentRun, jsTarget.browserProductionRun,
                jsTarget.browserRun, jsTarget.run,
            )
            // Users should be using Kobweb tasks instead of the standard multiplatform tasks, but they
            // probably don't know that. We do our best to work even in those cases, but warn the user to prefer
            // the Kobweb commands instead.
            jsRunTasks.forEach { taskName ->
                project.tasks.namedOrNull(taskName)?.configure {
                    doFirst {
                        logger.error("With Kobweb, you should run `gradlew kobwebStart` instead. Some site behavior may not work.")
                    }
                }
            }

            // Register generated sources directly to compileKotlin task so that KSP doesn't process them
            project.tasks.named<Kotlin2JsCompile>(jsTarget.compileKotlin) {
                source(kobwebGenSiteEntryTask)
            }

            project.kotlin.sourceSets.named(jsTarget.mainSourceSet) {
                resources.srcDir(kobwebCopySupplementalResourcesTask)
                resources.srcDir(kobwebCopyWorkerJsOutputTask)
            }

            // When exporting, both dev + production webpack actions are triggered - dev for the temporary server
            // that runs, and production for generating the final JS file for the site. However, these tasks share some
            // output directories (see https://youtrack.jetbrains.com/issue/KT-56305), so the following order
            // declaration is needed for gradle to be happy. Note also that we don't configure the task directly by its
            // name, as it may not yet exist (for some reason). Pending https://github.com/gradle/gradle/issues/16543,
            // we simply match it by its name amongst all tasks of its type.
            project.tasks
                .matching { it.name == jsTarget.compileProductionExecutableKotlin }
                .configureEach { mustRunAfter(kobwebStartTask) }

            kobwebStartTask.configure {
                // PROD env uses files copied over into a site folder by the export task, so it doesn't need to trigger
                // much.
                if (env == ServerEnvironment.DEV) {
                    val webpackTask = project.tasks.named(jsTarget.browserDevelopmentWebpack)
                    dependsOn(webpackTask)
                }
            }

            kobwebExportTask.configure {
                appFrontendMetadataFile.set(project.kspFrontendFile(jsTarget))
                // Exporting ALWAYS spins up a dev server, so that way it loads the files it needs from dev locations
                // before outputting them into a final prod folder.
                check(env == ServerEnvironment.DEV)

                dependsOn(kobwebCleanSiteTask)
                dependsOn(kobwebCreateServerScriptsTask)
                dependsOn(kobwebStartTask)
                dependsOn(project.tasks.namedOrNull(jsTarget.browserProductionWebpack))
            }
        }
        project.buildTargets.withType<KotlinJvmTarget>().configureEach {
            val jvmTarget = JvmTarget(this)

            project.setupKspJvm(jvmTarget)

            // PROD env uses files copied over into a site folder by the export task, so it doesn't need to trigger
            // much.
            kobwebStartTask.configure {
                if (env == ServerEnvironment.DEV) {
                    // If this site has server routes, make sure we built the jar that our servers can load
                    dependsOn(project.tasks.namedOrNull(jvmTarget.jar))
                }
            }

            val kobwebGenApisFactoryTask = project.tasks
                .register<KobwebGenerateApisFactoryTask>("kobwebGenApisFactory", kobwebBlock)

            kobwebGenApisFactoryTask.configure {
                kspGenFile.set(project.kspBackendFile(jvmTarget))
            }

            // Register generated sources directly to compileKotlin task so that KSP doesn't process them
            project.tasks.named<KotlinJvmCompile>(jvmTarget.compileKotlin) {
                source(kobwebGenApisFactoryTask)
            }
        }

        // Convenience task in case you quickly want to run all "kobwebGen..." tasks
        project.tasks.register("kobwebGenAll") {
            group = "kobweb"
            description = "Run all Kobweb code generation tasks"

            dependsOn(project.tasks.withType<KobwebGenerateTask>())
        }
    }
}

/**
 * Method provided for users to call if they generate their own Gradle task that generates some JS (frontend) code.
 *
 * Calling this ensures that their task will be triggered before the relevant Kobweb compilation task.
 */
@Deprecated(
    "Add the task outputs to the source set directly instead. Note that you may have to adjust the task to output a directory instead of a file.",
    ReplaceWith("kotlin.sourceSets.getByName(\"jsMain\").kotlin.srcDir(task)"),
)
fun Project.notifyKobwebAboutFrontendCodeGeneratingTask(task: Task) {
    tasks.matching { it.name == jsTarget.kspKotlin }.configureEach { dependsOn(task) }
}

@Deprecated(
    "Add the task outputs to the source set directly instead. Note that you may have to adjust the task to output a directory instead of a file.",
    ReplaceWith("kotlin.sourceSets.getByName(\"jsMain\").kotlin.srcDir(task)"),
)
fun Project.notifyKobwebAboutFrontendCodeGeneratingTask(task: TaskProvider<*>) {
    tasks.matching { it.name == jsTarget.kspKotlin }.configureEach { dependsOn(task) }
}

/**
 * Method provided for users to call if they generate their own Gradle task that generates some JVM (server) code.
 *
 * Calling this ensures that their task will be triggered before the relevant Kobweb compilation task.
 */
@Deprecated(
    "Add the task outputs to the source set directly instead. Note that you may have to adjust the task to output a directory instead of a file.",
    ReplaceWith("kotlin.sourceSets.getByName(\"jvmMain\").kotlin.srcDir(task)"),
)
fun Project.notifyKobwebAboutBackendCodeGeneratingTask(task: Task) {
    tasks.matching { it.name == jvmTarget?.kspKotlin }.configureEach { dependsOn(task) }
}

@Deprecated(
    "Add the task outputs to the source set directly instead. Note that you may have to adjust the task to output a directory instead of a file.",
    ReplaceWith("kotlin.sourceSets.getByName(\"jvmMain\").kotlin.srcDir(task)"),
)
fun Project.notifyKobwebAboutBackendCodeGeneratingTask(task: TaskProvider<*>) {
    tasks.matching { it.name == jvmTarget?.kspKotlin }.configureEach { dependsOn(task) }
}

/**
 * Inform Kobweb about a task that generates a jar that should be copied into the server's plugins directory.
 *
 * Users can always manually copy over server jars into their .kobweb/server/plugins directory, but this method
 * automates a common use case where the user is developing the plugin locally, and it would be frustrating to remember
 * to copy the new jar over each time something has changed.
 *
 * This method will create an intermediate task that copies the jar into the plugins directory, and then hooks up task
 * dependencies so that it will be called automatically before the Kobweb server runs.
 */
@Deprecated(
    "This approach has been simplified. Please replace it with `dependencies { kobwebServerPlugin(...) }` instead (where `dependencies` is the top-level dependencies block and \"...\" is the dependency that represents a kobweb server plugin jar)."
)
fun Project.notifyKobwebAboutServerPluginTask(
    jarTask: TaskProvider<Jar>,
    name: String = "copy${project.name.kebabCaseToTitleCamelCase()}JarToKobwebServerPluginsDir"
) {
    val copyKobwebServerPluginTask = tasks.register<Copy>(name) {
        from(jarTask)
        destinationDir = project.projectDir.resolve(".kobweb/server/plugins")
    }

    tasks.named("kobwebStart") {
        dependsOn(copyKobwebServerPluginTask)
    }
}

val Project.kobwebBuildTarget get() = project.extra["kobwebBuildTarget"] as BuildTarget
