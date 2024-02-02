package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import javax.inject.Inject

abstract class KobwebCopySupplementalResourcesTask @Inject constructor(@get:Internal val appBlock: AppBlock) :
    KobwebCopyTask("Copy and make available index.html & all public/ resources from any libraries to the final site") {

    @get:InputFile
    abstract val indexFile: RegularFileProperty

    @OutputDirectory
    fun getGenResDir() = appBlock.getGenJsResRoot("supplemental")

    private fun getGenPublicRoot() = getGenResDir().get().asFile.resolve(publicPath.get())

    @TaskAction
    fun execute() {
        val publicFilesPattern = PatternSet().apply {
            include("public/**")
        }

        val resourceData = runtimeClasspath.asFileTree.toKobwebOutputByPattern(publicFilesPattern)

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
