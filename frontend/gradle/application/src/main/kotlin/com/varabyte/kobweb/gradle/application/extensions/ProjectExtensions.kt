package com.varabyte.kobweb.gradle.application.extensions

import com.varabyte.kobweb.gradle.application.kmp.kotlin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.File

enum class TargetPlatform {
    JS,
    JVM,
}

private fun TargetPlatform.toSourceSetName(): String {
    return when (this) {
        TargetPlatform.JS -> "jsMain"
        TargetPlatform.JVM -> "jvmMain"
    }
}

private fun Project.getRoots(
    platform: TargetPlatform,
    sourceSetToDirSet: (KotlinSourceSet) -> SourceDirectorySet
): Sequence<File> {
    return project.kotlin.sourceSets.asSequence()
        .filter { sourceSet -> sourceSet.name == platform.toSourceSetName() }
        .flatMap { sourceSet -> sourceSetToDirSet(sourceSet).srcDirs }
}

fun Project.getSourceRoots(platform: TargetPlatform): Sequence<File> {
    return project.getRoots(platform) { sourceSet -> sourceSet.kotlin }
}

fun Project.getResourceRoots(platform: TargetPlatform): Sequence<File> =
    project.getRoots(platform) { sourceSet -> sourceSet.resources }

private fun Project.getFiles(
    platform: TargetPlatform,
    sourceSetToDirSet: (KotlinSourceSet) -> SourceDirectorySet
): Sequence<File> {
    return project.getRoots(platform, sourceSetToDirSet)
        .flatMap { root -> root.walkBottomUp() }
        .filter { it.isFile }
}

fun Project.getSourceFiles(platform: TargetPlatform): Sequence<File> {
    return project.getFiles(platform) { sourceSet -> sourceSet.kotlin }
        .filter { it.extension == "kt" }
}

fun Project.getResourceFiles(platform: TargetPlatform): Sequence<File> =
    project.getFiles(platform) { sourceSet -> sourceSet.resources }

/**
 * Using a [Project], get the fully qualified packages name, e.g. ".pages" -> "org.example.pages"
 */
fun Project.getQualifiedPackage(pkg: String): String {
    return when {
        pkg.startsWith('.') -> "${project.group}$pkg"
        else -> pkg
    }
}