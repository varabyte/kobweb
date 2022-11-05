package com.varabyte.kobweb.gradle.application.util

import com.varabyte.kobweb.gradle.core.KOBWEB_METADATA_SUBFOLDER
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.util.PatternSet
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

/**
 * Create a fat jar for use by a Kobweb server.
 *
 * The Kobweb server is designed to load a single jar responsible for providing
 *
 * TODO(#179): Take in a boolean parameter that does dead code elimination if true.
 */
fun KotlinJvmTarget.kobwebServerJar(archiveFileName: String) {
    project.tasks.named("jvmJar", Jar::class.java).configure {
        this.archiveFileName.set(archiveFileName)

        val classpathProvider = project.configurations.named("jvmRuntimeClasspath")
        inputs.files(classpathProvider)

        doFirst {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            val classpath = classpathProvider.get()
            val patterns = PatternSet().apply {
                exclude("$KOBWEB_METADATA_SUBFOLDER/**")
            }
            from(classpath.map { if (it.isDirectory) it else project.zipTree(it).matching(patterns) })
        }
    }
}

fun KotlinJsIrTarget.includeDependencyPublicResourcesInJar() {
    project.tasks.named("jsJar", Jar::class.java).configure {
        val classpathProvider = project.configurations.named("jsRuntimeClasspath")
        inputs.files(classpathProvider)

        doFirst {
            duplicatesStrategy = DuplicatesStrategy.WARN
            val classpath = classpathProvider.get()
            val patterns = PatternSet().apply {
                include("public/**")
            }
            from(classpath.map { if (it.isDirectory) it else project.zipTree(it).matching(patterns) })
        }
    }
}