package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.common.lang.packageConcat
import com.varabyte.kobweb.common.lang.toPackageName
import com.varabyte.kobweb.common.path.toUnixSeparators
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.gradle.core.util.getResourceFilesWithRoots
import com.varabyte.kobweb.gradle.core.util.getResourceRoots
import com.varabyte.kobweb.gradle.core.util.prefixQualifiedPackage
import com.varabyte.kobwebx.gradle.markdown.KotlinRenderer
import com.varabyte.kobwebx.gradle.markdown.MarkdownHandlers
import com.varabyte.kobwebx.gradle.markdown.MarkdownConfig
import com.varabyte.kobwebx.gradle.markdown.MarkdownFeatures
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class ConvertMarkdownTask @Inject constructor(
    private val kobwebBlock: KobwebBlock,
    private val markdownConfig: MarkdownConfig,
) : DefaultTask() {
    init {
        description = "Convert markdown files found in the project's resources path to source code in the final project"
    }

    private val markdownHandlers =
        (markdownConfig as ExtensionAware).extensions.getByName("handlers") as MarkdownHandlers
    private val markdownFeatures =
        (markdownConfig as ExtensionAware).extensions.getByName("features") as MarkdownFeatures

    private fun getMarkdownRoots(): Sequence<File> = project.getResourceRoots(project.jsTarget)
        .map { root -> root.resolve(markdownConfig.markdownPath.get()) }

    private fun getMarkdownFilesWithRoots(): List<RootAndFile> {
        val mdRoots = getMarkdownRoots()
        return project.getResourceFilesWithRoots(project.jsTarget)
            .filter { rootAndFile -> rootAndFile.file.extension == "md" }
            .mapNotNull { rootAndFile ->
                mdRoots.find { mdRoot -> rootAndFile.file.startsWith(mdRoot) }
                    ?.let { mdRoot -> RootAndFile(mdRoot, rootAndFile.file) }
            }
            .toList()
    }

    @InputFiles
    fun getMarkdownFiles(): List<File> {
        return getMarkdownFilesWithRoots().map { it.file }
    }

    @OutputDirectory
    fun getGenDir(): File =
        kobwebBlock.getGenJsSrcRoot(project).resolve(
            project.prefixQualifiedPackage(kobwebBlock.pagesPackage.get()).replace(".", "/")
        )

    @TaskAction
    fun execute() {
        val parser = markdownFeatures.createParser()
        getMarkdownFilesWithRoots().forEach { rootAndFile ->
            val mdFile = rootAndFile.file
            val mdPathRel = rootAndFile.relativeFile.toUnixSeparators()

            val parts = mdPathRel.split('/')
            val dirParts = parts.subList(0, parts.lastIndex)
            val packageParts = dirParts.map { it.toPackageName() }

            for (i in dirParts.indices) {
                if (dirParts[i] != packageParts[i]) {
                    // If not a match, that means the path that the markdown file is coming from is not compatible with
                    // Java package names, e.g. "2021" was converted to "_2021". This is fine -- we just need to tell
                    // Kobweb about the mapping.

                    val subpackage = packageParts.subList(0, i + 1)

                    File(getGenDir(), "${subpackage.joinToString("/")}/PackageMapping.kt")
                        // Multiple markdown files in the same folder will try to write this over and over again; we
                        // can skip after the first time
                        .takeIf { !it.exists() }
                        ?.let { mappingFile ->
                            mappingFile.parentFile.mkdirs()
                            mappingFile.writeText("""
                                @file:PackageMapping("${dirParts[i]}")

                                package ${project.prefixQualifiedPackage(kobwebBlock.pagesPackage.get().packageConcat(
                                subpackage.joinToString(".")
                            )) }

                                import com.varabyte.kobweb.core.PackageMapping
                            """.trimIndent())
                    }
                }
            }

            val ktFileName = mdFile.nameWithoutExtension
            File(getGenDir(), "${packageParts.joinToString("/")}/$ktFileName.kt").let { outputFile ->
                outputFile.parentFile.mkdirs()
                val mdPackage = project.prefixQualifiedPackage(
                    kobwebBlock.pagesPackage.get().packageConcat(packageParts.joinToString("."))
                )

                // The suggested replacement for "capitalize" is awful
                @Suppress("DEPRECATION")
                val funName = "${ktFileName.capitalize()}Page"
                val ktRenderer = KotlinRenderer(project, mdPathRel, markdownHandlers, mdPackage, funName)
                outputFile.writeText(ktRenderer.render(parser.parse(mdFile.readText())))
            }
        }
    }
}