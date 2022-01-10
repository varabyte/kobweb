package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.extensions.RootAndFile
import com.varabyte.kobweb.gradle.application.extensions.TargetPlatform
import com.varabyte.kobweb.gradle.application.extensions.getResourceFilesWithRoots
import com.varabyte.kobweb.gradle.application.extensions.getResourceRoots
import com.varabyte.kobweb.gradle.application.extensions.prefixQualifiedPackage
import com.varabyte.kobwebx.gradle.markdown.KotlinRenderer
import com.varabyte.kobwebx.gradle.markdown.MarkdownComponents
import com.varabyte.kobwebx.gradle.markdown.MarkdownConfig
import com.varabyte.kobwebx.gradle.markdown.MarkdownFeatures
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCallExtension
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class ConvertMarkdownTask @Inject constructor(
    private val kobwebConfig: KobwebConfig,
    private val markdownConfig: MarkdownConfig,
) : DefaultTask() {
    init {
        description = "Convert markdown files found in the project's resources path to source code in the final project"
    }

    private val markdownComponents =
        (markdownConfig as ExtensionAware).extensions.getByName("components") as MarkdownComponents
    private val markdownFeatures =
        (markdownConfig as ExtensionAware).extensions.getByName("features") as MarkdownFeatures

    private fun getMarkdownRoots(): Sequence<File> = project.getResourceRoots(TargetPlatform.JS)
        .map { root -> File(root, markdownConfig.markdownPath.get()) }

    private fun getMarkdownFilesWithRoots(): List<RootAndFile> {
        val mdRoots = getMarkdownRoots()
        return project.getResourceFilesWithRoots(TargetPlatform.JS)
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
        kobwebConfig.getGenJsSrcRoot(project).resolve(
            project.prefixQualifiedPackage(kobwebConfig.pagesPackage.get()).replace(".", "/")
        )

    @TaskAction
    fun execute() {
        getMarkdownFilesWithRoots().forEach { rootAndFile ->
            val mdFile = rootAndFile.file
            val mdFileRel = rootAndFile.relativeFile

            val extensions = mutableListOf<Extension>()
            markdownFeatures.run {
                if (autolink.get()) {
                    extensions.add(AutolinkExtension.create())
                }
                if (frontMatter.get()) {
                    extensions.add(YamlFrontMatterExtension.create())
                }
                if (kobwebCall.get()) {
                    extensions.add(KobwebCallExtension.create(kobwebCallDelimiters.get()))
                }
                if (tables.get()) {
                    extensions.add(TablesExtension.create())
                }
                if (taskList.get()) {
                    extensions.add(TaskListItemsExtension.create())
                }
            }

            val parser = Parser.builder()
                .extensions(extensions)
                .build()

            val parts = mdFileRel.path.split("/")
            val dirParts = parts.subList(0, parts.lastIndex)

            val mdPackage =
                kobwebConfig.pagesPackage.get() + if (dirParts.isNotEmpty()) ".${dirParts.joinToString(".")}" else ""
            val funName = mdFileRel.nameWithoutExtension

            File(getGenDir(), "${dirParts.joinToString("/")}/$funName.kt").let { outputFile ->
                outputFile.parentFile.mkdirs()

                val ktRenderer = KotlinRenderer(project, mdFileRel.path, markdownComponents, mdPackage, funName)
                outputFile.writeText(ktRenderer.render(parser.parse(mdFile.readText())))
            }
        }
    }
}