package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.path.invariantSeparatorsPath
import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.tasks.KobwebModuleTask
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_MODULE
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_WORKER_SUBFOLDER
import com.varabyte.kobweb.ksp.KOBWEB_PUBLIC_WORKER_ROOT
import org.gradle.api.GradleException
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.FileTree
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import java.io.File
import javax.inject.Inject

/**
 * Copy JS output which is automatically packaged into metadata by the Kobweb worker plugin.
 */
abstract class KobwebCopyWorkerJsOutputTask @Inject constructor(kobwebBlock: KobwebBlock) : KobwebModuleTask(
    kobwebBlock,
    "Copy any JS output files from any Kobweb worker dependencies and copy them to the final site's resources"
) {
    @get:Inject
    abstract val fileSystemOperations: FileSystemOperations

    @get:Inject
    abstract val objectFactory: ObjectFactory

    @get:Inject
    abstract val archiveOperations: ArchiveOperations

    @InputFiles
    fun getRuntimeClasspath() = project.configurations.named(project.jsTarget.runtimeClasspath)

    @OutputDirectory
    fun getGenResDir() = kobwebBlock.getGenJsResRoot<AppBlock>(project).resolve("worker")

    private fun getGenPublicRoot() = getGenResDir().resolve(kobwebBlock.publicPath.get())

    @TaskAction
    fun execute() {
        val classpath = getRuntimeClasspath().get()
        val kobwebModulePattern = PatternSet().apply {
            include(KOBWEB_METADATA_MODULE)
        }
        val workerOutpusFilesPattern = PatternSet().apply {
            include("$KOBWEB_METADATA_WORKER_SUBFOLDER/**")
        }

        // Find the "worker.js" and "worker.js.map" files bundled into the jars that were built with by Kobweb worker
        // plugin.
        fun FileTree.toWorkerOutputResources(jar: File): List<Pair<File, RootAndFile>> {
            val fileTree = this
            if (fileTree.matching(kobwebModulePattern).isEmpty) return emptyList()

            return buildList {
                fileTree.matching(workerOutpusFilesPattern)
                    .visit {
                        if (this.isDirectory) return@visit
                        val root =
                            File(file.absolutePath.invariantSeparatorsPath.removeSuffix(relativePath))
                        add(jar to RootAndFile(root, file))
                    }
            }
        }

        // We track "copied file path to source jar path" so we can warn users if there's a collision.
        // It's very unlikely that this will happen, since for it to occur, both the project group and the worker name
        // will have to be the same across different projects. But it could happen if a user duplicates a
        // worker project and forgets to update either the group name or worker name.
        val copiedFiles = mutableMapOf<String, String>()
        val workerOutputData = classpath.flatMap { jar ->
            if (jar.isDirectory) {
                objectFactory.fileTree().from(jar).toWorkerOutputResources(jar)
            } else {
                try {
                    archiveOperations.zipTree(jar).toWorkerOutputResources(jar)
                } catch (ex: Exception) {
                    // NOTE: I used to catch ZipException here, but it became GradleException at some point?? So
                    // let's just be safe and block all exceptions here. It sucks if this task crashes here because
                    // not being able to unzip a non-zip file is not really a big deal.

                    // It's possible to get a classpath file that's not a jar -- npm dependencies are like this --
                    // at which point the file isn't a zip nor a directory. Such dependencies will never contain
                    // Kobweb resources, so we don't care about them. Just skip 'em!
                    emptyList()
                }
            }
        }

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
                        throw GradleException("You are attempting to use two different worker dependencies that both have the same group and worker name: $sourceJar and ${jar.name}. They are incompatible and you cannot use both. If you own the project for either, consider changing the Gradle group or the worker name (via `kobweb { worker { name.set(...) } }`) on one or the other to avoid this naming collision.")
                    }

                    into(targetFile.relativeTo(getGenPublicRoot()).parentFile) // relative to top-level "into" dir
                }
            }
        }
    }
}
