package com.varabyte.kobweb.gradle.library.util

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Convenience method for configuring a `kotlin` block with initialization boilerplate used by Kobweb libraries.
 *
 * @param includeServer If true, this configuration will also initialize a JVM target (in addition to a JS Browser
 *   target) which will ultimately get included in a Kobweb application's server.
 * @param jsTargetName The name to use for this project's javascript target. For example, this affects the prefix of the
 *   Gradle tasks generated for building this project.
 * @param jvmTargetName The name to use for this project's JVM target. For example, this affects the prefix of the
 *   generated server jar name. This value will have no effect if [includeServer] is set to false.
 */
fun KotlinMultiplatformExtension.configAsKobwebLibrary(
    includeServer: Boolean = false,
    jsTargetName: String = "js",
    jvmTargetName: String = "jvm",
) {
    js(jsTargetName) {
        kobwebLibraryBrowser()
    }
    if (includeServer) {
        jvm(jvmTargetName)
    }
}
