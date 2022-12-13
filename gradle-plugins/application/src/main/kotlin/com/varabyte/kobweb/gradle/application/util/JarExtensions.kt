package com.varabyte.kobweb.gradle.application.util

import com.varabyte.kobweb.gradle.core.KOBWEB_METADATA_SUBFOLDER
import com.varabyte.kobweb.gradle.core.util.suggestKobwebProjectName
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
 *
 * @param kobwebName A name to use as the base of the output jar file. If left blank, a name will be
 *   created using [suggestKobwebProjectName]
 */
fun KotlinJvmTarget.kobwebServerJar(kobwebName: String? = null) {
    val archiveFileName = (kobwebName ?: project.suggestKobwebProjectName()).addSuffix(".jar")
    val jvmTargetName = name
    project.tasks.named("${jvmTargetName}Jar", Jar::class.java).configure {
        this.archiveFileName.set(archiveFileName)

        val classpathProvider = project.configurations.named("${jvmTargetName}RuntimeClasspath")
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

internal fun KotlinJsIrTarget.includeDependencyPublicResourcesInJar() {
    val jsTargetName = name
    project.tasks.named("${jsTargetName}Jar", Jar::class.java).configure {
        val classpathProvider = project.configurations.named("${jsTargetName}RuntimeClasspath")
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