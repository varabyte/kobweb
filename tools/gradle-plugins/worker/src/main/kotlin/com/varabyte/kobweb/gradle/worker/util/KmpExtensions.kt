package com.varabyte.kobweb.gradle.worker.util

import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.worker.extensions.worker
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.util.suffixIfNot

/**
 * Convenience method for configuring a `kotlin` block with initialization boilerplate used by Kobweb workers.
 *
 * @param jsTargetName The name to use for this project's javascript target. For example, this affects the generated
 *   Gradle tasks generated for building this project.
 */
fun KotlinMultiplatformExtension.configAsKobwebWorker(
    jsTargetName: String = "js",
) {
    js(jsTargetName) {
        val name = project.kobwebBlock.worker.name.get().removeSuffix(".js")
        binaries.executable()
        this.moduleName = name
        browser {
            commonWebpackConfig {
                outputFileName = name.suffixIfNot(".js")
            }
        }
    }
}
