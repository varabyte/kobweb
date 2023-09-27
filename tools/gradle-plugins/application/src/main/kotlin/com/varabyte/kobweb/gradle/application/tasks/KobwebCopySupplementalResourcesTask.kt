package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.path.toUnixSeparators
import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.tasks.KobwebModuleTask
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import java.io.File
import javax.inject.Inject

abstract class KobwebCopySupplementalResourcesTask @Inject constructor(
    kobwebBlock: KobwebBlock,
    @get:InputFile val indexFile: Provider<RegularFile>,
) : KobwebModuleTask(
    kobwebBlock,
    "Copy and make available index.html & all public/ resources from any libraries to the final site"
) {

    @InputFiles
    fun getRuntimeClasspath() = project.configurations.named(project.jsTarget.runtimeClasspath)

    @OutputDirectory
    fun getGenResDir() = kobwebBlock.getGenJsResRoot<AppBlock>(project).resolve("app")

    private fun getGenPublicRoot() = getGenResDir().resolve(kobwebBlock.publicPath.get())

    @TaskAction
    fun execute() {
        val classpath = getRuntimeClasspath().get()
        val patterns = PatternSet().apply {
            include("public/**")
        }

        classpath
            .flatMap { jar ->
                if (jar.isDirectory) {
                    val fileTree = project.fileTree(jar)
                    fileTree.matching(patterns).files.map { file -> jar to RootAndFile(fileTree.dir, file) }
                } else {
                    try {
                        buildList {
                            project.zipTree(jar).matching(patterns).visit {
                                if (!this.isDirectory) {
                                    // relativePath always uses forward slash. Convert the absolute path to forward slashes,
                                    // therefore, before removing the suffix, or else Windows breaks.
                                    val root = File(file.absolutePath.toUnixSeparators().removeSuffix(relativePath))
                                    add(jar to RootAndFile(root, file))
                                }
                            }
                        }
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
            }.forEach { (jar, rootAndFile) ->
                val targetFile =
                    getGenPublicRoot().resolve(rootAndFile.relativeFile.toUnixSeparators().removePrefix("public/"))
                if (targetFile.exists() && !rootAndFile.file.readBytes().contentEquals(targetFile.readBytes())) {
                    logger.warn("Overwriting ${rootAndFile.relativeFile} with the public resource found in ${jar.name}")
                }
                rootAndFile.file.copyTo(targetFile, overwrite = true)
            }

        indexFile.get().asFile.copyTo(getGenPublicRoot().resolve("index.html"), overwrite = true)
    }
}
