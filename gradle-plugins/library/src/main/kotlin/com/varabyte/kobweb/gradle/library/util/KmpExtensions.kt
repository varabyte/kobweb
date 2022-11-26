package com.varabyte.kobweb.gradle.library.util

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun KotlinMultiplatformExtension.configAsKobwebLibrary(includeServer: Boolean = false) {
    js(IR) {
        kobwebLibraryBrowser()
    }
    if (includeServer) {
        jvm()
    }
}
