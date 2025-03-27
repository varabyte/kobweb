package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.common.lang.packageConcat
import com.varabyte.kobweb.common.lang.packageFromFile
import com.varabyte.kobweb.common.text.ensureSurrounded
import com.varabyte.kobweb.gradle.core.util.LoggingReporter
import com.varabyte.kobweb.project.common.PackageUtils
import com.varabyte.kobwebx.gradle.markdown.KotlinRenderer
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownFeatures
import com.varabyte.kobwebx.gradle.markdown.handlers.MarkdownHandlers
import com.varabyte.kobwebx.gradle.markdown.util.NodeCache
import com.varabyte.kobwebx.gradle.markdown.util.RouteUtils
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import java.io.File
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.relativeTo

abstract class ConvertMarkdownTask @Inject constructor(markdownBlock: MarkdownBlock) :
    MarkdownTask(
        markdownBlock,
        "Convert markdown files found in the project's resources path to source code in the final project"
    ) {

    /**
     * The path from the codebase root to this project, which is information we pass down into the generated code.
     *
     * For example, if the path to your Kobweb project is `"myproject/web/site", then this value will be set to
     * "web/site".
     */
    @get:Input
    abstract val projectRoot: Property<String>

    @Nested
    val markdownHandlers = markdownBlock.extensions.getByType<MarkdownHandlers>()

    @Nested
    val markdownFeatures = markdownBlock.extensions.getByType<MarkdownFeatures>()

    @Input
    val markdownDefaultRoot = markdownBlock.defaultRoot

    @Input
    val markdownImports = markdownBlock.imports

    @get:Input
    abstract val dependsOnMarkdownArtifact: Property<Boolean>

    @OutputDirectory
    fun getGenDir(): Provider<Directory> {
        return markdownBlock.getGenJsSrcRoot("convert")
    }

    @TaskAction
    fun execute() {
        getGenDir().get().asFile.clearDirectory()
        val nodeCache = NodeCache(
            parser = markdownFeatures.createParser(),
            roots = markdownFolders.get().flatMap { it.files.files }.toSet()
        )

        markdownFiles.visit {
            if (isDirectory) return@visit

            val pkgBase = markdownFolders.findTargetPackage(rootDir) ?: run {
                logger.warn("Please report we could not find a target source root for markdown folder \"$rootDir\". Skipping converting \"$path\".")
                return@visit
            }

            val sourcePath = relativePath.toPath()
            val absolutePackage = pkgBase.packageConcat(sourcePath.packageFromFile())
            val outputRootPath = Path(PackageUtils.packageToPath(absolutePackage))

            nodeCache.metadata[nodeCache[file]] = NodeCache.Metadata.Entry(
                projectRoot.get(),
                rootDir.relativeTo(projectLayout.projectDirectory.asFile).toPath(),
                relativePath.toPath(),
                outputRootPath,
                absolutePackage,
                // Route should only be set for pages
                if (outputRootPath.startsWith(pagesPath)) {
                    outputRootPath.relativeTo(pagesPath).invariantSeparatorsPathString.ensureSurrounded("/") +
                        RouteUtils.getSlug(sourcePath.toFile())
                } else null,
            )
        }

        markdownFiles.visit {
            if (isDirectory) return@visit
            val node = try { nodeCache[file] } catch (_: IllegalArgumentException) { null } ?: return@visit
            val metadata = nodeCache.metadata.getValue(node)

            val sourcePath = relativePath.toPath()

            val outputFileName = sourcePath.nameWithoutExtension.replaceFirstChar { it.uppercase() }
            val outputRootPathStr = nodeCache.metadata.getValue(node).outputRootPath.invariantSeparatorsPathString

            File(
                getGenDir().get().asFile.resolve(outputRootPathStr),
                "$outputFileName.kt"
            ).let { outputFile ->
                outputFile.parentFile.mkdirs()

                val ktRenderer = KotlinRenderer(
                    projectGroup.get().toString(),
                    nodeCache,
                    markdownDefaultRoot.get().takeUnless { it.isBlank() },
                    markdownImports.get(),
                    markdownHandlers,
                    funName = sourcePath.capitalizedNameWithoutExtension +
                        "Page".takeIf { metadata.routeWithSlug != null }.orEmpty(),
                    dependsOnMarkdownArtifact.get(),
                    LoggingReporter(logger),
                )
                outputFile.writeText(ktRenderer.render(nodeCache[file]))
            }
        }
    }
}
