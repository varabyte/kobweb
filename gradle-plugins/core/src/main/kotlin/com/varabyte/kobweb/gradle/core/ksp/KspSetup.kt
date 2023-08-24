package com.varabyte.kobweb.gradle.core.ksp

import com.google.devtools.ksp.gradle.KspExtension
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.project.common.PackageUtils
import com.varabyte.kobweb.gradle.core.tasks.KobwebGenerateMetadataTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

const val KSP_PAGES_PACKAGE_KEY = "kobweb.pagesPackage" // TODO: location
const val KSP_API_PACKAGE_KEY = "kobweb.apiPackage" // TODO: location

fun setupKsp(project: Project, kobwebBlock: KobwebBlock) {
    project.pluginManager.apply("com.google.devtools.ksp")

    val kspExtension = project.extensions.getByType<KspExtension>()

    val taskCollection = TaskCollections(project.tasks)
    val kobwebGenTasks = project.tasks.withType<KobwebGenerateMetadataTask<*>>() // idk if this is needed
    val kobwebGenSiteEntry = project.tasks.matching { it.name == "kobwebGenSiteEntry" }
    val kobwebGenApi = project.tasks.matching { it.name == "kobwebGenApisFactory" }
    val kspCommonMainKotlinMetadata =
        project.tasks.matching { it.name == "kspCommonMainKotlinMetadata" } // doesn't exist if js only

    taskCollection.compileKotlinJs.configureEach {
        dependsOn(kspCommonMainKotlinMetadata)
    }

    taskCollection.compileKotlinJvm.configureEach {
        dependsOn(kspCommonMainKotlinMetadata)
    }

    // for playground
    val kspProcessorVersion = ""
    val kspDependency = "com.varabyte.kobweb:ksp-processor:$kspProcessorVersion"

    // for outside use
//    val kspProcessorVersion = "0.13.12-SNAPSHOT-ksp49" // matching lib version
//    val kspDependency = "com.varabyte.kobweb:kobweb-ksp-processor:$kspProcessorVersion"

//        println(project.plugins.withId("com.varabyte.kobweb.library"))

//        project.dependencies {
//            add("kspCommonMainMetadata", kspDependency)
////            add("kspJs", kspDependency)
//        }

    val kotlinMppExtension = project.extensions.getByType<KotlinMultiplatformExtension>()
    project.afterEvaluate {
        dependencies {
            add("kspJs", kspDependency)
            if (configurations.findByName("kspJvm") != null) {
                add("kspJvm", kspDependency)
            }
        }
//            kotlinMppExtension.sourceSets.getByName("commonMain").kotlin
//                .srcDir("build/generated/ksp/metadata/commonMain/kotlin")
//            kotlinMppExtension.sourceSets.getByName("jsMain").kotlin
//                .srcDir("build/generated/ksp/js/jsMain/kotlin")
    }


    taskCollection.kspKotlinJs.configureEach {
        // TODO: we currently set this kep using task.project.group since the group can be different from different modules
        // however, task.project is not recommended to be used, what are our alternatives?
        kspExtension.arg(
            KSP_PAGES_PACKAGE_KEY,
            PackageUtils.resolvePackageShortcut(this.project.group.toString(), kobwebBlock.pagesPackage.get()),
        )
        dependsOn(kspCommonMainKotlinMetadata)
        mustRunAfter(kobwebGenTasks)
        mustRunAfter(kobwebGenSiteEntry)
    }

    taskCollection.kspKotlinJvm.configureEach {
        // TODO: we currently set this kep using task.project.group since the group can be different from different modules
        // however, task.project is not recommended to be used, what are our alternatives?
        kspExtension.arg(
            KSP_API_PACKAGE_KEY,
            PackageUtils.resolvePackageShortcut(this.project.group.toString(), kobwebBlock.apiPackage.get()),
        )
        dependsOn(kspCommonMainKotlinMetadata)
        mustRunAfter(kobwebGenApi)
    }
}


// copied from kvision
// TODO: replace this with the current setup (which importantly supports custom-named source sets)
private class TaskCollections(private val tasks: TaskContainer) {
//    val jsProcessResources: TaskCollection<Copy>
//        get() = collection("jsProcessResources")

    val compileKotlinJs: TaskCollection<KotlinCompile<*>>
        get() = collection("compileKotlinJs")

    val compileKotlinJvm: TaskCollection<KotlinCompile<*>>
        get() = collection("compileKotlinJvm")

//    val jsBrowserProductionWebpack: TaskCollection<KotlinWebpack>
//        get() = collection("jsBrowserProductionWebpack")
//
//    val workerBrowserProductionWebpack: TaskCollection<Task>
//        get() = collection("workerBrowserProductionWebpack")
//
//    val kotlinNpmInstall: TaskCollection<Task>
//        get() = collection("kotlinNpmInstall")

    val kspKotlinJs: TaskCollection<Task>
        get() = collection("kspKotlinJs")

    val kspKotlinJvm: TaskCollection<Task>
        get() = collection("kspKotlinJvm")

    private inline fun <reified T : Task> collection(taskName: String): TaskCollection<T> =
        tasks.withType<T>().matching { it.name == taskName }
}
