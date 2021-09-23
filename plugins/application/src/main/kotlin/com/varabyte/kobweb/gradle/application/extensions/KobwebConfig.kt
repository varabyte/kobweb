@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.extensions

import com.varabyte.kobweb.gradle.application.GENERATED_ROOT
import com.varabyte.kobweb.gradle.application.kmp.kotlin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFiles
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.File

private fun File.isDescendantOf(maybeAncestor: File): Boolean {
    var curr: File? = this
    while (curr != null) {
        if (curr == maybeAncestor) {
            return true
        }
        curr = curr.parentFile
    }
    return false
}

abstract class KobwebConfig {
    /**
     * The string path to the Kobweb conf.yaml file, relative to the project root.
     */
    abstract val confFile: Property<String>

    /**
     * The string path to the root where generated code will be written to, relative to the project root.
     */
    abstract val genDir: Property<String>

    /**
     * The root package of all pages.
     *
     * Any composable function not under this root will be ignored, even if annotated by @Page.
     *
     * An initial '.' means this should be prefixed by the project group, e.g. ".pages" -> "com.example.pages"
     */
    abstract val pagesPackage: Property<String>

    /**
     * The path of public resources inside the project's resources folder, e.g. "public" ->
     * "src/jsMain/resources/public"
     */
    abstract val publicPath: Property<String>

    init {
        confFile.convention(".kobweb/conf.yaml")
        genDir.convention(GENERATED_ROOT)

        pagesPackage.convention(".pages")
        publicPath.convention("public")
    }

    private fun getFiles(project: Project, rootDirProducer: (KotlinSourceSet) -> FileCollection): Sequence<File> {
        val genDirFile = project.projectDir.resolve(genDir.get())

        return project.kotlin.sourceSets.asSequence()
            .filter { sourceSet -> sourceSet.name == "jsMain" }
            .flatMap { sourceSet ->
                rootDirProducer(sourceSet)
                    .filter { rootDir -> !rootDir.isDescendantOf(genDirFile) }
                    .flatMap { rootDir ->
                        rootDir.walkBottomUp().filter { it.isFile }
                    }
            }

    }

    @InputFiles
    fun getSourceFiles(project: Project): List<File> {
        return getFiles(project) { sourceSet -> sourceSet.kotlin.sourceDirectories }
            .filter { it.extension == "kt" }
            .toList()
    }

    @InputFiles
    fun getResourceFiles(project: Project): List<File> = getFiles(project) { sourceSet -> sourceSet.resources }.toList()
}