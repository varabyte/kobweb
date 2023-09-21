package com.varabyte.kobweb.gradle.application.util

import com.varabyte.kobweb.common.text.suffixIfNot
import com.varabyte.kobweb.gradle.core.KOBWEB_METADATA_SUBFOLDER
import com.varabyte.kobweb.gradle.core.util.suggestKobwebProjectName
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.util.PatternSet
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

/**
 * Create a fat jar for use by a Kobweb server.
 *
 * The Kobweb server loads a single jar which is responsible for providing all functionality backing its server routes.
 *
 * TODO(#179): Allow configuring DCE for the final server jar.
 *
 * @param kobwebName A name to use as the base of the output jar file. If left blank, a name will be created using
 * [suggestKobwebProjectName]. If you change this later, you should also check your .kobweb/conf.yaml file and update
 * relevant entries.
 */
fun KotlinJvmTarget.kobwebServerJar(kobwebName: String? = null) {
    val archiveFileName = (kobwebName ?: project.suggestKobwebProjectName()).suffixIfNot(".jar")
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
