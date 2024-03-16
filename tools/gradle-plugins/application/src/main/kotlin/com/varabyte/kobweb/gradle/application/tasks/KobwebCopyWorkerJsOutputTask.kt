package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_WORKER_SUBFOLDER
import com.varabyte.kobweb.ksp.KOBWEB_PUBLIC_WORKER_ROOT
import org.gradle.api.GradleException
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import javax.inject.Inject

/**
 * Copy JS output which is automatically packaged into metadata by the Kobweb worker plugin.
 */
abstract class KobwebCopyWorkerJsOutputTask @Inject constructor(private val appBlock: AppBlock) : KobwebCopyTask(
    "Copy any JS output files from any Kobweb worker dependencies and copy them to the final site's resources"
) {
    @OutputDirectory
    fun getGenResDir() = appBlock.getGenJsResRoot("worker")

    private fun getGenPublicRoot() = getGenResDir().get().asFile.resolve(publicPath.get())

    @TaskAction
    fun execute() {
        val workerOutputsFilesPattern = PatternSet().apply {
            include("$KOBWEB_METADATA_WORKER_SUBFOLDER/**")
        }

        // We track "copied file path to source jar path" so we can warn users if there's a collision.
        // It's very unlikely that this will happen, since for it to occur, both the project group and the worker name
        // will have to be the same across different projects. But it could happen if a user duplicates a
        // worker project and forgets to update either the group name or worker name.
        val copiedFiles = mutableMapOf<String, String>()
        val workerOutputData = runtimeClasspath.toKobwebOutputByPattern(workerOutputsFilesPattern)

        fileSystemOperations.sync {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            into(getGenPublicRoot())
            workerOutputData.forEach { (jar, rootAndFile) ->
                from(rootAndFile.file) {
                    val targetFile = getGenPublicRoot()
                        .resolve(KOBWEB_PUBLIC_WORKER_ROOT)
                        .resolve(
                            rootAndFile.relativeFile.invariantSeparatorsPath.removePrefix("$KOBWEB_METADATA_WORKER_SUBFOLDER/")
                        )

                    copiedFiles.putIfAbsent(targetFile.absolutePath, jar.name)?.let { sourceJar ->
                        throw GradleException("You are attempting to use two different worker dependencies that both have the same group and worker name: $sourceJar and ${jar.name}. They are incompatible and you cannot use both. If you own the project for either, consider changing the Gradle group or the worker name (via `configAsKobwebWorker(...)`) on one or the other to avoid this naming collision.")
                    }

                    into(targetFile.relativeTo(getGenPublicRoot()).parentFile) // relative to top-level "into" dir
                }
            }
        }
    }
}
