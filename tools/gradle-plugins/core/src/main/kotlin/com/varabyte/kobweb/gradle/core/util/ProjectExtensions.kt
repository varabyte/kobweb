package com.varabyte.kobweb.gradle.core.util

import com.varabyte.kobweb.gradle.core.kmp.TargetPlatform
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobweb.gradle.core.tasks.KobwebGenerateModuleMetadataTask
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.File
import java.security.MessageDigest
import kotlin.math.absoluteValue

private fun Project.getRoots(
    platform: TargetPlatform<*>,
    sourceSetToDirSet: (KotlinSourceSet) -> SourceDirectorySet
): Sequence<File> {
    return project.kotlin.sourceSets.asSequence()
        .filter { sourceSet -> sourceSet.name == platform.mainSourceSet }
        .flatMap { sourceSet -> sourceSetToDirSet(sourceSet).srcDirs }
}

fun Project.getResourceRoots(platform: TargetPlatform<*>): Sequence<File> =
    project.getRoots(platform) { sourceSet -> sourceSet.resources }

class RootAndFile(val root: File, val file: File) {
    val relativeFile get() = file.relativeTo(root)
}

private fun Project.getFilesWithRoots(
    platform: TargetPlatform<*>,
    sourceSetToDirSet: (KotlinSourceSet) -> SourceDirectorySet
): Sequence<RootAndFile> {
    return project.getRoots(platform, sourceSetToDirSet)
        .flatMap { root ->
            root.walkBottomUp()
                .filter { it.isFile && it.startsWith(root) }
                .map { file -> RootAndFile(root, file) }
        }
}

fun Project.getResourceFilesWithRoots(platform: TargetPlatform<*>): Sequence<RootAndFile> {
    return project.getFilesWithRoots(platform) { sourceSet -> sourceSet.resources }
}

fun Project.getBuildScripts(): Sequence<File> {
    return sequenceOf("build.gradle", "build.gradle.kts")
        .map { script -> project.layout.projectDirectory.file(script).asFile }
        .filter { it.exists() }
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

/**
 * Returns true if one of this module's direct dependencies (no transitive dependencies included) is named [name].
 *
 * This method should be called in an [Project.afterEvaluate] block, or else it will always return false.
 */
fun Project.hasJsDependencyNamed(name: String): Boolean {
    check(project.state.executed)
    return configurations.asSequence()
        .flatMap { config -> config.dependencies }
        .any { dependency -> dependency.name == name }
}

/**
 * Like [hasJsDependencyNamed] but includes transitive dependencies as well.
 *
 * This method should only be called after a project is finished being configured, e.g. inside a task action.
 */
fun Project.hasTransitiveJsDependencyNamed(name: String): Boolean {
    return configurations.findByName(jsTarget.runtimeClasspath)
        ?.resolvedConfiguration
        ?.resolvedArtifacts
        ?.any { artifact -> artifact.moduleVersion.id.name == name }
        ?: false
}

/**
 * Suggest a name to use to represent this Kobweb project.
 *
 * This name could be used, for example, when generating output resources for a Kobweb application, like the server
 * jar name or the main javascript file.
 */
fun Project.suggestKobwebProjectName() = project.group.toString().replace('.', '-')

/**
 * All Kobweb dependencies should include a module.json to identify themselves as such.
 *
 * Kobweb application plugins will check for it and use its presence to distinguish which of its dependencies are
 * Kobweb artifacts.
 *
 * This method will fail with an exception if the core plugin is not applied before calling it.
 */
fun Project.generateModuleMetadataFor(target: TargetPlatform<*>) {
    project.tasks.named<ProcessResources>(target.processResources) {
        from(project.tasks.withType<KobwebGenerateModuleMetadataTask>())
    }
}

/**
 * Generate a consistent, unique ID for this project.
 *
 * The UID is generated based on a combination of the project's group and name. The result will be a hashed, uppercase
 * string using hex values (e.g. "F169AE3A...").
 *
 * The string generated is fairly long, but you can definitely truncate it and still have reasonable randomness from
 * that. For example, if you truncate this to 4 characters, the chance of a collision is 1 out of 16^4, or 1 out of
 * 65,536.
 */
fun Project.toUidString(): String {
    val rawText = "${project.group}:${project.name}"
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(rawText.toByteArray(Charsets.UTF_8))

    return hash.joinToString("") { "%02x".format(it) }.uppercase()
}
