package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import javax.inject.Inject

abstract class KobwebCopySupplementalResourcesTask @Inject constructor(
    kobwebBlock: KobwebBlock,
    @get:InputFile val indexFile: Provider<RegularFile>,
) : KobwebCopyTask(
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
        val publicFilesPattern = PatternSet().apply {
            include("public/**")
        }

        val resourceData = classpath.toKobwebOutputByPattern(publicFilesPattern)

        fileSystemOperations.sync {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            into(getGenPublicRoot())
            from(indexFile)
            resourceData.forEach { (jar, rootAndFile) ->
                from(rootAndFile.file) {
                    // If here, we are sure that "jar" is a Kobweb library (because the kobweb module.json file was present)
                    // and we are processing one of its public resources. Remove the "public" prefix from the file path
                    // because it's going to get copied into a target "public" directory.
                    val targetFile = getGenPublicRoot()
                        .resolve(rootAndFile.relativeFile.invariantSeparatorsPath.removePrefix("public/"))
                    if (targetFile.exists() && !rootAndFile.file.readBytes().contentEquals(targetFile.readBytes())) {
                        logger.warn("Overwriting ${rootAndFile.relativeFile} with the public resource found in ${jar.name}")
                    }
                    into(targetFile.relativeTo(getGenPublicRoot()).parentFile) // relative to top-level "into" dir
                }
            }
        }
    }
}
