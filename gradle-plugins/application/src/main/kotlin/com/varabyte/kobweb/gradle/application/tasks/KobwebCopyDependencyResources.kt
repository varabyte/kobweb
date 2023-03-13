@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.path.toUnixSeparators
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.tasks.KobwebModuleTask
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import java.io.File
import javax.inject.Inject

abstract class KobwebCopyDependencyResources @Inject constructor(
    kobwebBlock: KobwebBlock,
) : KobwebModuleTask(kobwebBlock, "Copy and make available all public/ resources from the application and any libraries to the final site") {

    @InputFiles
    fun getRuntimeClasspath() = project.configurations.named(project.jsTarget.runtimeClasspath)

    @OutputDirectory
    fun getGenPublicRoot() = File(kobwebBlock.getGenJsResRoot(project), kobwebBlock.publicPath.get())

    @TaskAction
    fun execute() {
        getGenPublicRoot().mkdirs()

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
                    val unzipped = mutableListOf<Pair<File, RootAndFile>>()
                    project.zipTree(jar).matching(patterns).visit {
                        if (!this.isDirectory) {
                            unzipped.add(jar to RootAndFile(File(file.absolutePath.removeSuffix(relativePath)), file))
                        }
                    }

                    unzipped
                }
            }.forEach { (jar, rootAndFile) ->
                val targetFile = File(getGenPublicRoot(), rootAndFile.relativeFile.toUnixSeparators().removePrefix("public/"))
                if (targetFile.exists() && !rootAndFile.file.readBytes().contentEquals(targetFile.readBytes())) {
                    project.logger.warn("Overwriting ${rootAndFile.relativeFile} with the public resource found in ${jar.name}")
                }
                rootAndFile.file.copyTo(targetFile, overwrite = true)
            }
    }
}