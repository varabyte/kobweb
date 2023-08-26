package com.varabyte.kobweb.gradle.library.util

import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

/**
 * Handle registering a JS browser target for this module (configured for a Kobweb library).
 *
 * Note: Most people won't call this directly, but should instead use [configAsKobwebLibrary].
 */
fun KotlinJsTargetDsl.kobwebLibraryBrowser() {
    browser()
}