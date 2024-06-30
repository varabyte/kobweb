package com.varabyte.kobweb.gradle.core.util

import com.varabyte.kobweb.gradle.core.kmp.TargetPlatform
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobweb.gradle.core.tasks.KobwebGenerateModuleMetadataTask
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.File
import java.security.MessageDigest

private fun Project.getRoots(
    platform: TargetPlatform<*>,
    sourceSetToDirSet: (KotlinSourceSet) -> SourceDirectorySet
): Sequence<File> {
    return project.kotlin.sourceSets.asSequence()
        .filter { sourceSet -> sourceSet.name == platform.mainSourceSet }
        .flatMap { sourceSet -> sourceSetToDirSet(sourceSet).srcDirs }
}

fun Project.getResourceSources(target: TargetPlatform<*>): Provider<SourceDirectorySet> =
    project.kotlin.sourceSets.named(target.mainSourceSet).map { it.resources }

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

fun Project.getSourceFilesWithRoots(platform: TargetPlatform<*>): Sequence<RootAndFile> {
    return project.getFilesWithRoots(platform) { sourceSet -> sourceSet.kotlin }
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
 * Return the Gradle build script in the current project.
 *
 * If a task depends on a Kobweb block property, then you want to make sure it is invalidated if that changes. However,
 * passing the block into the task with an `@Input` annotation is not always possible, as the block may not be
 * serializable, which is a requirement for Gradle inputs and outputs. (For example, this can happen if one of the
 * properties accepts a lambda which pulls in some property into its closure.)
 *
 * As a workaround for those cases, you can do something like this:
 *
 * ```
 * abstract class ExampleTask(private val someBlock: SomeBlock) {
 *   @InputFiles
 *   @PathSensitive(PathSensitivity.RELATIVE)
 *   fun getBuildScripts(): List<File> = projectLayout.getBuildScripts().toList()
 * }
 * ```
 *
 * Note that the above case doesn't play friendly with Gradle's configuration cache, so it should only be used as a last
 * resort.
 *
 * The method technically returns a collection of files, which could happen if the project has both a `build.gradle` and
 * a `build.gradle.kts` file. However, in practice, we always expect this to return a single entry, as most projects
 * would only ever have one or the other.
 */
// TODO: If possible, kill this approach and find a way to make Kobweb blocks safe to use as inputs even with lambdas.
fun ProjectLayout.getBuildScripts(): List<File> {
    return sequenceOf("build.gradle", "build.gradle.kts")
        .mapNotNull { script -> this.projectDirectory.file(script).asFile.takeIf { it.exists() } }
        .toList()
}
