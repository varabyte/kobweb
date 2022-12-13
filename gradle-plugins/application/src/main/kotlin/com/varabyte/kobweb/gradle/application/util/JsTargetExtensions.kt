package com.varabyte.kobweb.gradle.application.util

import com.varabyte.kobweb.gradle.core.util.suggestKobwebProjectName
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

/**
 * @param kobwebName A name to use as the base of the output JS file. If left blank, a name will be
 *   created using [suggestKobwebProjectName].
 */
fun KotlinJsTargetDsl.kobwebApplicationBrowser(kobwebName: String? = null) {
    @Suppress("NAME_SHADOWING")
    val kobwebName = kobwebName ?: project.suggestKobwebProjectName()

    this.moduleName = kobwebName
    browser {
        commonWebpackConfig {
            outputFileName = kobwebName.addSuffix(".js")
        }
    }
    binaries.executable()
}
