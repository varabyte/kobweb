package com.varabyte.kobweb.gradle.library.util

import com.varabyte.kobweb.gradle.core.util.suggestKobwebModuleName
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

fun KotlinJsTargetDsl.kobwebLibraryBrowser() {
    browser()
}