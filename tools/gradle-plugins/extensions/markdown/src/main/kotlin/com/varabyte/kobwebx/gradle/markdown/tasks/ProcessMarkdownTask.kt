package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.util.RootAndFile
import com.varabyte.kobweb.gradle.core.util.getResourceFilesWithRoots
import com.varabyte.kobweb.gradle.core.util.getResourceRoots
import com.varabyte.kobweb.gradle.core.util.prefixQualifiedPackage
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownData
import com.varabyte.kobwebx.gradle.markdown.MarkdownFeatures
import com.varabyte.kobwebx.gradle.markdown.yamlStringToKotlinString
import org.commonmark.ext.front.matter.YamlFrontMatterBlock
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.CustomBlock
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import java.io.File
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

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
) : DefaultTask() {
    init {
        description = ""
    }

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
    fun getGenDir(): File = kobwebBlock.getGenJsSrcRoot<MarkdownBlock>(project).resolve(
        project.prefixQualifiedPackage(kobwebBlock.pagesPackage.get()).replace(".", "/")
    )


    @TaskAction
    fun execute() {
        val parser = markdownFeatures.createParser()
        val markdownDataList = getMarkdownFiles().map {
            val visitor = MarkdownVisitor()
            parser
                .parse(it.readText())
                .accept(visitor)
            MarkdownData(
                filePath = it.path,
                frontMatter = visitor.frontMatter
            )
        }
        File(getGenDir(), markdownBlock.ktFileName.get()).let { outputFile ->
            outputFile.parentFile.mkdirs()
            outputFile.writeText(
                buildString {
                    appendLine(
                        """
                        |// This file is generated. Modify the build script if you need to change it.
                        |
                        |package ${markdownBlock.packageName.get()}
                        |
                        """.trimMargin()
                    )
                    append(markdownBlock.process.get()(markdownDataList))
                }
            )
        }
    }
}
