package com.varabyte.kobweb.gradle.core.icons

import com.varabyte.kobweb.gradle.core.extensions.IconsBlock
import com.varabyte.kobweb.gradle.core.extensions.createIconsBlock
import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.buildTargets
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobweb.gradle.core.tasks.KobwebFetchIconsTask
import com.varabyte.kobweb.gradle.core.tasks.KobwebGenerateIconsTask
import org.gradle.api.Project
import org.gradle.api.provider.Provider
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

    val iconVersionSpecs = provider {
        iconsBlock.iconSets.mapValues { (_, iconSetBlock) -> iconSetBlock.version.get() }
    }
    val kobwebFetchIconsTask = tasks.register<KobwebFetchIconsTask>("kobwebFetchIcons") {
        versionSpecs.set(iconVersionSpecs)
        storeDir.set(iconsBlock.storeDir)
    }
    val kobwebGenerateIconsTask = tasks.register<KobwebGenerateIconsTask>("kobwebGenerateIcons") {
        versionSpecs.set(iconVersionSpecs)
        storeDir.set(iconsBlock.storeDir)
        projectGroup.set(provider { project.group.toString() })
        rootPackage.set(iconsBlock.rootPackage)
        visibility.set(iconsBlock.visibility)
        outputDir.set(layout.buildDirectory.dir(kobwebBlock.baseGenDir.map { "$it/icons/src/jsMain/kotlin" }))
        // So that `./gradlew kobwebFetchIcons build` generates from freshly fetched data.
        // Normally mustRunAfter is a sign that inputs and outputs aren't set up correctly, but here, we don't want to
        // trigger a fetch, because that would force a network request on every build.
        mustRunAfter(kobwebFetchIconsTask)
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
        buildTargets.withType<KotlinJsIrTarget>().configureEach {
            kotlin.sourceSets.named(JsTarget(this).mainSourceSet) {
                kotlin.srcDir(kobwebGenerateIconsTask)
            }
        }
    }
}

/**
 * The configured icon sets, mapped to the exact versions their data was fetched for, e.g. `"fa" to "6.5.2"`.
 *
 * Sets that haven't been fetched yet are left out; `kobwebGenerateIcons` reports those.
 */
fun IconsBlock.lockedVersions(): Provider<Map<String, String>> {
    return providers.provider {
        iconSets.keys.mapNotNull { id ->
            IconsLock.readFrom(IconsLock.fileFor(storeDir.asFile, id))?.let { lock -> id to lock.version }
        }.toMap()
    }
}
