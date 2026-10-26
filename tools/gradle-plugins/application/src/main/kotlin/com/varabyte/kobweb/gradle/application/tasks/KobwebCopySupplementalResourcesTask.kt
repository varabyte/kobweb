package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.core.metadata.publicPathOrDefault
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = "This is just a copy task (and caching the files would not avoid the copy).")
abstract class KobwebCopySupplementalResourcesTask @Inject constructor(
    private val appBlock: AppBlock,
    @get:InputFile @get:PathSensitive(PathSensitivity.NONE)
    val indexFile: Provider<RegularFile>,
) : KobwebCopyTask("Copy and make available index.html & all public/ resources from any libraries to the final site") {
    @OutputDirectory
    fun getGenResDir() = appBlock.getGenJsResRoot("supplemental")

    private fun getGenPublicRoot() = getGenResDir().get().asFile.resolve(publicPath.get())

    @TaskAction
    fun execute() {
        val resourceData = runtimeClasspath.toKobwebOutputByPattern { metadata ->
            PatternSet().apply {
                include("${metadata.publicPathOrDefault}/**")
            }
        }

        fileSystemOperations.sync {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            into(getGenPublicRoot())
            from(indexFile)
            resourceData.forEach { moduleFile ->
                val rootAndFile = moduleFile.rootAndFile
                from(rootAndFile.file) {
                    // If here, we are sure that "jar" is a Kobweb module (because the kobweb module.json file was present)
                    // and we are processing one of its public resources. Remove the module's public path prefix from
                    // the file path because it's going to get copied into this site's public directory.
                    val targetFile = getGenPublicRoot().resolve(
                        rootAndFile.relativeFile.invariantSeparatorsPath
                            .removePrefix("${moduleFile.metadata.publicPathOrDefault}/")
                    )
                    if (targetFile.exists() && !rootAndFile.file.readBytes().contentEquals(targetFile.readBytes())) {
                        logger.warn("Overwriting ${rootAndFile.relativeFile} with the public resource found in ${moduleFile.jar.name}")
                    }
                    into(targetFile.relativeTo(getGenPublicRoot()).parentFile) // relative to top-level "into" dir
                }
            }
        }
    }
}
