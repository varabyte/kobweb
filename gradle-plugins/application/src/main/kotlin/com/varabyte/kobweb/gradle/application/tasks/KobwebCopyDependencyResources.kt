@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.path.toUnixSeparators
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.tasks.KobwebModuleTask
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.project.conf.KobwebConf
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import java.io.File
import javax.inject.Inject

private fun File.parentStartingWith(prefix: String): File {
    var match: File? = this
    while (match != null) {
        if (match.name.startsWith(prefix)) return match
        match = match.parentFile
    }
    error("No parent found matching file prefix $prefix")
}

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
                    // Annoyingly, zipTree won't tell you the root dir! But we need it to find the relative directory we
                    // need to copy to. We use the jar name instead, as the zip file creates a path like
                    // /some/tmp/path/jarname.jar_198403920482/path/to/zipped/file
                    val zipTree = project.zipTree(jar)
                    zipTree.matching(patterns).files.map { file ->
                        jar to RootAndFile(file.parentStartingWith(jar.name), file)
                    }
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