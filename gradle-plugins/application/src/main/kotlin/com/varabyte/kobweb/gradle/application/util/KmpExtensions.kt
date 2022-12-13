package com.varabyte.kobweb.gradle.application.util

import com.varabyte.kobweb.gradle.core.util.suggestKobwebProjectName
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Convenience method for configuring this `kotlin` block with Kobweb initialization boilerplate.
 *
 * @param moduleName The name you'd like to use to represent this project. For example, the final JS output file will
 *   use this name. If not specified, a name will be created using [suggestKobwebProjectName].
 * @param includeServer If true, this configuration will also initialize a JVM target which will be used for producing
 *   a jar that is used by a Kobweb server.
 * @param jsTargetName The name to use for this project's javascript target. For example, this affects the generated
 *   Gradle tasks generated for building this project.
 * @param jvmTargetName The name to use for this project's JVM target. For example, this affects the generated server
 *   jar name. This value will be ignored if [includeServer] is set to false.
 */
fun KotlinMultiplatformExtension.configAsKobwebApplication(
    moduleName: String? = null,
    includeServer: Boolean = false,
    jsTargetName: String = "js",
    jvmTargetName: String = "jvm",
) {
    js(jsTargetName, IR) {
        kobwebApplicationBrowser(moduleName)
    }
    if (includeServer) {
        jvm(jvmTargetName) {
            kobwebServerJar(moduleName)
        }
    }
}
