package com.varabyte.kobweb.gradle.application.util

import com.varabyte.kobweb.gradle.core.util.suggestKobwebModuleName
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

fun KotlinJsTargetDsl.kobwebApplicationBrowser(moduleName: String? = null) {
    @Suppress("NAME_SHADOWING")
    val moduleName = moduleName ?: project.suggestKobwebModuleName()

    this.moduleName = moduleName
    browser {
        commonWebpackConfig {
            outputFileName = moduleName.addSuffix(".js")
        }
    }
    binaries.executable()
}
