@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.extensions

import com.varabyte.kobweb.gradle.application.GENERATED_ROOT
import com.varabyte.kobweb.gradle.application.JS_RESOURCE_SUFFIX
import com.varabyte.kobweb.gradle.application.JS_SRC_SUFFIX
import com.varabyte.kobweb.gradle.application.JVM_SRC_SUFFIX
import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File

abstract class KobwebConfig {
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

    init {
        genDir.convention(GENERATED_ROOT)
        pagesPackage.convention(".pages")
        apiPackage.convention(".api")
        publicPath.convention("public")
    }

    fun getGenJsSrcRoot(project: Project): File = project.layout.buildDirectory.dir("${genDir.get()}$JS_SRC_SUFFIX").get().asFile
    fun getGenJsResRoot(project: Project): File = project.layout.buildDirectory.dir("${genDir.get()}$JS_RESOURCE_SUFFIX").get().asFile
    fun getGenJvmSrcRoot(project: Project): File = project.layout.buildDirectory.dir("${genDir.get()}$JVM_SRC_SUFFIX").get().asFile
}