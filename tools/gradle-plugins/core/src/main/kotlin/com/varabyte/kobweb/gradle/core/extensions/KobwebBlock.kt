@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.core.extensions

import com.varabyte.kobweb.gradle.core.GENERATED_ROOT
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.get
import java.io.File

/**
 * A gradle block used for initializing values that configure a Kobweb project.
 *
 * This class also exposes a handful of methods useful for querying the project.
 */
abstract class KobwebBlock {
    /**
     * The string path to the root where generated code will be written to, relative to the project build directory.
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
     * The root package of all apis.
     *
     * Any function not under this root will be ignored, even if annotated by @Api.
     *
     * An initial '.' means this should be prefixed by the project group, e.g. ".api" -> "com.example.api"
     */
    abstract val apiPackage: Property<String>

    /**
     * The path of public resources inside the project's resources folder, e.g. "public" ->
     * "src/jsMain/resources/public"
     */
    abstract val publicPath: Property<String>

    /**
     * Whether the Kobweb gradle plugin should automatically include its corresponding KSP processor dependency.
     *
     * A KSP processor dependency is required for Kobweb to work, but setting this to false allows manually depending on
     * a different version of the processor.
     */
    abstract val includeKspDependency: Property<Boolean>

    init {
        genDir.convention(GENERATED_ROOT)
        pagesPackage.convention(".pages")
        apiPackage.convention(".api")
        publicPath.convention("public")
        includeKspDependency.convention(true)
    }

    fun getGenJsSrcRoot(project: Project): File {
        val jsSrcSuffix = project.jsTarget.srcSuffix
        return project.layout.buildDirectory.dir("${genDir.get()}$jsSrcSuffix").get().asFile
    }

    fun getGenJsResRoot(project: Project): File {
        val jsResourceSuffix = project.jsTarget.resourceSuffix
        return project.layout.buildDirectory.dir("${genDir.get()}$jsResourceSuffix").get().asFile
    }

    fun getGenJvmSrcRoot(project: Project): File {
        val jvmSrcSuffix = (project.jvmTarget ?: error("No JVM target defined")).srcSuffix
        return project.layout.buildDirectory.dir("${genDir.get()}$jvmSrcSuffix").get().asFile
    }

    fun getGenJvmResRoot(project: Project): File {
        val jvmResSuffix = (project.jvmTarget ?: error("No JVM target defined")).resourceSuffix
        return project.layout.buildDirectory.dir("${genDir.get()}$jvmResSuffix").get().asFile
    }
}

val Project.kobwebBlock get() = project.extensions["kobweb"] as KobwebBlock
