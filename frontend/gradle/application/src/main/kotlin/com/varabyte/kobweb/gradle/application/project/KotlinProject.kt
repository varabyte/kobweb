package com.varabyte.kobweb.gradle.application.project

import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CompilerConfiguration

// For now, we're directly parsing Kotlin code using the embedded Kotlin compiler. This is a temporary approach.
// In the future, this should use KSP to navigate through source files. See also: Bug #4
fun createKotlinProject(): Project {
    return KotlinCoreEnvironment.createForProduction(
        Disposer.newDisposable(),
        CompilerConfiguration(),
        EnvironmentConfigFiles.JS_CONFIG_FILES
    ).project
}