package com.varabyte.kobweb.gradle.worker.util

import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.worker.extensions.worker
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.util.suffixIfNot

/**
 * Convenience method for configuring a `kotlin` block with initialization boilerplate used by Kobweb workers.
 *
 * @param workerName The name that will get used for this worker's script name. You do not need to include the ".js"
 *   suffix here, e.g. "my-worker" will generate "my-worker.js".
 * @param jsTargetName The name to use for this project's javascript target. For example, this affects the generated
 *   Gradle tasks generated for building this project.
 */
fun KotlinMultiplatformExtension.configAsKobwebWorker(
    workerName: String? = null,
    jsTargetName: String = "js",
) {
    js(jsTargetName) {
        if (workerName != null) {
            // This will either be a generic name with a randomized suffix (if the user didn't set it) or the value
            // of `kobweb.worker.name`.
            val previousWorkerName = project.kobwebBlock.worker.name.get()
            project.kobwebBlock.worker.name.convention(workerName)

            // name.get will either return the convention we just set OR a specific name entered by the user, if they
            // set `kobweb.worker.name` direclty.
            project.kobwebBlock.worker.name.get().let { workerBlockName ->
                if (workerBlockName != workerName) {
                    project.logger.warn("w: `kobweb.worker.name` is set to \"$workerBlockName\" while `configAsKobwebWorker` is being called with \"$workerName\". In this case, \"$workerName\" will be ignored. Consider removing the `kobweb.worker.name` setting and passing \"$workerBlockName\" into `configAsKobwebWorker`, or leave the `workerName` parameter unset instead.")
                } else if (workerBlockName == previousWorkerName) {
                    // If here, this means `kobweb.worker.name` was explicitly set by the user. Otherwise, it would be
                    // a randomized name which is unlikely for the user to have also happened to set.
                    project.logger.warn("w: `kobweb.worker.name` and `configAsKobwebWorker` are both being set to \"$workerName\". This is redundant, and you should consider deleting the line that sets `kobweb.worker.name` or leaving the `workerName` parameter unset in `configAsKobwebWorker`.")
                }
            }
        }

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
