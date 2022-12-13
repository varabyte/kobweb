package com.varabyte.kobweb.gradle.library.util

import com.varabyte.kobweb.gradle.core.util.suggestKobwebProjectName
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Convenience method for configuring this `kotlin` block with Kobweb initialization boilerplate.
 *
 * @param includeServer If true, this configuration will also initialize a JVM target which will be used for producing
 *   artifacts that can be included downstream when building a jar that is used by a Kobweb server.
 * @param jsTargetName The name to use for this project's javascript target. For example, this affects the generated
 *   Gradle tasks generated for building this project.
 * @param jvmTargetName The name to use for this project's JVM target. For example, this affects the generated server
 *   jar name. This value will be ignored if [includeServer] is set to false.
 */
fun KotlinMultiplatformExtension.configAsKobwebLibrary(
    includeServer: Boolean = false,
    jsTargetName: String = "js",
    jvmTargetName: String = "jvm",
) {
    js(jsTargetName, IR) {
        kobwebLibraryBrowser()
    }
    if (includeServer) {
        jvm(jvmTargetName)
    }
}
