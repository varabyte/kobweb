package com.varabyte.kobweb.gradle.core.util

import com.varabyte.kobweb.gradle.core.kmp.TargetPlatform
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobweb.gradle.core.tasks.KobwebGenerateModuleMetadataTask
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.configuration.BuildFeatures
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.util.GradleVersion
import java.io.File
import java.nio.file.Path
import java.security.MessageDigest

fun Project.getResourceSources(target: TargetPlatform<*>): Provider<SourceDirectorySet> =
    project.kotlin.sourceSets.named(target.mainSourceSet).map { it.resources }

class RootAndFile(val root: File, val file: File) {
    val relativeFile get() = file.relativeTo(root)
}

private fun Project.getDependencyResultsFromConfiguration(configurationName: String): List<ResolvedDependencyResult> {
    return configurations[configurationName].incoming.resolutionResult.allDependencies
        .mapNotNull { it as? ResolvedDependencyResult }
}

/**
 * Lazily returns a list of this module's direct JS dependencies (no transitive dependencies included).
 *
 * @see getTransitiveJsDependencyResults
 * @see hasDependencyNamed for a way to check if a specific dependency is included in this list.
 */
fun Project.getJsDependencyResults(): Provider<List<ResolvedDependencyResult>> {
    return provider { getDependencyResultsFromConfiguration(jsTarget.compileClasspath) }
}

/**
 * Lazily returns a list of this module's direct & transitive JS dependencies.
 *
 * @see getJsDependencyResults
 * @see hasDependencyNamed for a way to check if a specific dependency is included in this list.
 */
fun Project.getTransitiveJsDependencyResults(): Provider<List<ResolvedDependencyResult>> {
    return provider { getDependencyResultsFromConfiguration(jsTarget.runtimeClasspath) }
}

/**
 * Lazily returns a list of this module's direct JVM dependencies (no transitive dependencies included).
 *
 * @see getTransitiveJvmDependencyResults
 * @see hasDependencyNamed for a way to check if a specific dependency is included in this list.
 */
fun Project.getJvmDependencyResults(): Provider<List<ResolvedDependencyResult>> {
    return provider { jvmTarget?.compileClasspath?.let { getDependencyResultsFromConfiguration(it) } ?: emptyList() }
}

/**
 * Lazily returns a list of this module's direct & transitive JS dependencies.
 *
 * @see getJvmDependencyResults
 * @see hasDependencyNamed for a way to check if a specific dependency is included in this list.
 */
fun Project.getTransitiveJvmDependencyResults(): Provider<List<ResolvedDependencyResult>> {
    return provider { jvmTarget?.runtimeClasspath?.let { getDependencyResultsFromConfiguration(it) } ?: emptyList() }
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

/**
 * Return true if the Gradle "Isolated Projects" feature is enabled.
 *
 * The standard recommended way to do this is to inject the `BuildFeatures` API into your code and query it, but that
 * API was only added in Gradle 8.5, and I don't want the Kobweb plugin to crash people using older versions of Gradle.
 */
fun Project.isIsolatedProjectsActive(): Boolean {
    // Gradle 8.5+ introduced the BuildFeatures service starting in 8.5
    // https://docs.gradle.org/current/javadoc/org/gradle/api/configuration/BuildFeatures.html
    return if (GradleVersion.current() >= GradleVersion.version("8.5")) {
        val buildFeatures = this.serviceOf<BuildFeatures>()
        buildFeatures.isolatedProjects.active.getOrElse(false)
    } else {
        // Technically, experimental versions of isolated projects existed before 8.5, but I don't think we have
        // to worry about checking for them. Anyone who cares enough to use isolated projects will be using a much more
        // recent version of Gradle.
        false
    }
}
