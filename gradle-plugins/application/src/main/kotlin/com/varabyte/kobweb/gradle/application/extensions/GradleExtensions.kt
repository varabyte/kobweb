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

class RootAndFile(val root: File, val file: File) {
    val relativeFile get() = file.relativeTo(root)
}

private fun Project.getFilesWithRoots(
    platform: TargetPlatform,
    sourceSetToDirSet: (KotlinSourceSet) -> SourceDirectorySet
): Sequence<RootAndFile> {
    return project.getRoots(platform, sourceSetToDirSet)
        .flatMap { root ->
            root.walkBottomUp()
                .filter { it.isFile && it.startsWith(root) }
                .map { file -> RootAndFile(root, file) }
        }
}

fun Project.getSourceFilesWithRoots(platform: TargetPlatform): Sequence<RootAndFile> {
    return project.getFilesWithRoots(platform) { sourceSet -> sourceSet.kotlin }
        .filter { it.file.extension == "kt" }
}

fun Project.getResourceFilesWithRoots(platform: TargetPlatform): Sequence<RootAndFile> {
    return project.getFilesWithRoots(platform) { sourceSet -> sourceSet.resources }
}

fun Project.getSourceFiles(platform: TargetPlatform): Sequence<File> {
    return project.getSourceFilesWithRoots(platform).map { it.file }
}

fun Project.getResourceFiles(platform: TargetPlatform): Sequence<File> {
    return project.getResourceFilesWithRoots(platform).map { it.file }
}

/**
 * Using a [Project], get the fully qualified packages name, e.g. ".pages" -> "org.example.pages"
 */
fun Project.prefixQualifiedPackage(relPathMaybe: String): String {
    return when {
        relPathMaybe.startsWith('.') -> "${project.group}$relPathMaybe"
        else -> relPathMaybe
    }
}