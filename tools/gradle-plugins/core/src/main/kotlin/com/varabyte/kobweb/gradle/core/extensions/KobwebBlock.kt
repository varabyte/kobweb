@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.core.extensions

import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.getByType
import java.io.File

/**
 * A gradle block used for initializing values that configure a Kobweb project.
 *
 * This class also exposes a handful of methods useful for querying the project.
 */
abstract class KobwebBlock {
    // TODO: how should this deprecation work
    /**
     * The string path to the root where generated code will be written to, relative to the project build directory.
     */
    @Deprecated("", ReplaceWith("baseGenDir"))
    val genDir: Property<String>
        get() = baseGenDir

    /**
     * The string path to the root where generated code will be written to, relative to the project build directory.
     */
    abstract val baseGenDir: Property<String>

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

    /** The KSP processor dependency that should be applied to the project, in string dependency notation. */
    abstract val kspProcessorDependency: Property<String>

    init {
        genDir.convention("generated/kobweb")
        baseGenDir.convention("generated/kobweb")
        pagesPackage.convention(".pages")
        apiPackage.convention(".api")
        publicPath.convention("public")
        val kspProcessorVersion = "0.14.1-SNAPSHOT" // matching lib version
        val kspDependency = "com.varabyte.kobweb:kobweb-ksp-project-processors:$kspProcessorVersion"
        kspProcessorDependency.convention(kspDependency)
    }

    inline fun <reified T : FileGeneratingBlock> getGenJsSrcRoot(project: Project): File {
        val jsSrcSuffix = project.jsTarget.srcSuffix
        val genDir = (this as ExtensionAware).extensions.getByType<T>().genDir.get()
        return project.layout.buildDirectory.dir("$genDir$jsSrcSuffix").get().asFile
    }

    inline fun <reified T : FileGeneratingBlock> getGenJsResRoot(project: Project): File {
        val jsResourceSuffix = project.jsTarget.resourceSuffix
        val genDir = (this as ExtensionAware).extensions.getByType<T>().genDir.get()
        return project.layout.buildDirectory.dir("$genDir$jsResourceSuffix").get().asFile
    }

    inline fun <reified T : FileGeneratingBlock> getGenJvmSrcRoot(project: Project): File {
        val jvmSrcSuffix = (project.jvmTarget ?: error("No JVM target defined")).srcSuffix
        val genDir = (this as ExtensionAware).extensions.getByType<T>().genDir.get()
        return project.layout.buildDirectory.dir("$genDir$jvmSrcSuffix").get().asFile
    }
}

val Project.kobwebBlock get() = project.extensions.getByType<KobwebBlock>()
