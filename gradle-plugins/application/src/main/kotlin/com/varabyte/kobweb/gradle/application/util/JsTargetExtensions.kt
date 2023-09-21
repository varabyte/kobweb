package com.varabyte.kobweb.gradle.application.util

import com.varabyte.kobweb.common.text.suffixIfNot
import com.varabyte.kobweb.gradle.core.util.suggestKobwebProjectName
import org.gradle.api.Action
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

/**
 * Handle registering a JS browser target for this module (configured for a Kobweb application).
 *
 * Note: Most people won't call this directly, but should instead use [configAsKobwebApplication].
 *
 * @param kobwebName A name to use as the base of the output JS file. If left blank, a name will be created using
 *   [suggestKobwebProjectName]. If you change this later, you should also check your .kobweb/conf.yaml file and update
 *   relevant entries.
 */
fun KotlinJsTargetDsl.kobwebApplicationBrowser(kobwebName: String? = null) {
    @Suppress("NAME_SHADOWING")
    val kobwebName = kobwebName ?: project.suggestKobwebProjectName()

    this.moduleName = kobwebName
    browser {
        commonWebpackConfig(Action {
            outputFileName = kobwebName.suffixIfNot(".js")
        })
    }
    binaries.executable()
}
