package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.gradle.core.util.getResourceFilesWithRoots
import com.varabyte.kobweb.gradle.core.util.getResourceRoots
import com.varabyte.kobweb.gradle.core.util.prefixQualifiedPackage
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownEntry
import com.varabyte.kobwebx.gradle.markdown.MarkdownFeatures
import com.varabyte.kobwebx.gradle.markdown.yamlStringToKotlinString
import org.commonmark.ext.front.matter.YamlFrontMatterBlock
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.CustomBlock
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import java.io.File
import javax.inject.Inject

class MarkdownVisitor : AbstractVisitor() {
    private val _frontMatter = mutableMapOf<String, List<String>>()
    val frontMatter: Map<String, List<String>> = _frontMatter

    override fun visit(customBlock: CustomBlock) {
        if (customBlock is YamlFrontMatterBlock) {
            val yamlVisitor = YamlFrontMatterVisitor()
            customBlock.accept(yamlVisitor)
            _frontMatter.putAll(
                yamlVisitor.data
                    .mapValues { (_, values) ->
                        values.map { it.yamlStringToKotlinString() }
                    }
            )
        }
    }
}

abstract class ProcessMarkdownTask @Inject constructor(
    private val kobwebBlock: KobwebBlock,
    private val markdownBlock: MarkdownBlock,
) : KobwebTask("Processes markdown files found in the project's resources path to metadata and provides methods for user to add custom generated files to the final project") {

    private val markdownFeatures = markdownBlock.extensions.getByType<MarkdownFeatures>()

    private fun getMarkdownRoots(): Sequence<File> = project.getResourceRoots(project.jsTarget)
        .map { root -> root.resolve(markdownBlock.markdownPath.get()) }

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
    fun getGenSrcDir(): File = kobwebBlock.getGenJsSrcRoot<MarkdownBlock>(project).resolve(
        project.prefixQualifiedPackage(kobwebBlock.baseGenDir.get()).replace(".", "/")
    )

    @OutputDirectory
    fun getGenResDir(): File = kobwebBlock.getGenJsResRoot<MarkdownBlock>(project).resolve(
        project.prefixQualifiedPackage(kobwebBlock.publicPath.get()).replace(".", "/")
    )

    @TaskAction
    fun execute() {
        val parser = markdownFeatures.createParser()
        val markdownEntries = getMarkdownFiles().map {
            val visitor = MarkdownVisitor()
            parser
                .parse(it.readText())
                .accept(visitor)
            MarkdownEntry(
                filePath = it.path,
                frontMatter = visitor.frontMatter
            )
        }
        val process = markdownBlock.process.orNull ?: return
        val processScope = MarkdownBlock.ProcessScope()
        processScope.process(markdownEntries)

        processScope.markdownOutput.forEach { processNode ->
            File(getGenResDir(), processNode.path).let { outputFile ->
                outputFile.parentFile.mkdirs()
                outputFile.writeText(processNode.contents)
            }
        }
        processScope.kotlinOutput.forEach { processNode ->
            File(getGenSrcDir(), processNode.path).let { outputFile ->
                outputFile.parentFile.mkdirs()
                outputFile.writeText(processNode.contents)
            }
        }
    }
}
