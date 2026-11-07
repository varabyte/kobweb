package com.varabyte.kobweb.gradle.core.icons

import com.varabyte.kobweb.gradle.core.extensions.createIconsBlock
import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.buildTargets
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobweb.gradle.core.tasks.KobwebFetchIconsTask
import com.varabyte.kobweb.gradle.core.tasks.KobwebGenerateIconsTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget

/**
 * Adds the `kobweb { icons { ... } }` block and the tasks that fetch icon data and generate bindings from it.
 *
 * Only apps and library plugins should call this.
 */
fun Project.setupIcons() {
    val kobwebBlock = kobwebBlock
    val iconsBlock = kobwebBlock.createIconsBlock()

    val iconsStoreDir = layout.projectDirectory.dir("kobweb-store/icons")
    val iconVersions = provider {
        iconsBlock.iconSets.mapValues { (id, iconSetBlock) ->
            iconSetBlock.version.orNull
                ?: throw GradleException("The \"$id\" icon set in `kobweb { icons { ... } }` needs a version.")
        }
    }
    tasks.register<KobwebFetchIconsTask>("kobwebFetchIcons") {
        versions.set(iconVersions)
        storeDir.set(iconsStoreDir)
    }
    val kobwebGenerateIconsTask = tasks.register<KobwebGenerateIconsTask>("kobwebGenerateIcons") {
        versions.set(iconVersions)
        storeDir.set(iconsStoreDir)
        projectGroup.set(provider { project.group.toString() })
        rootPackage.set(iconsBlock.rootPackage)
        visibility.set(iconsBlock.visibility)
        outputDir.set(layout.buildDirectory.dir(kobwebBlock.baseGenDir.map { "$it/icons/src/jsMain/kotlin" }))
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
        buildTargets.withType<KotlinJsIrTarget>().configureEach {
            kotlin.sourceSets.named(JsTarget(this).mainSourceSet) {
                kotlin.srcDir(kobwebGenerateIconsTask)
            }
        }
    }
}
