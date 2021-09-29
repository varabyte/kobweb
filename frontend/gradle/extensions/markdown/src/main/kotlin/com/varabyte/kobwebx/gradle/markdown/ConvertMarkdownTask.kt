package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.extensions.TargetPlatform
import com.varabyte.kobweb.gradle.application.extensions.getResourceFiles
import com.varabyte.kobweb.gradle.application.extensions.getResourceRoots
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
import org.jetbrains.kotlin.util.suffixIfNot
import java.io.File
import javax.inject.Inject

abstract class ConvertMarkdownTask @Inject constructor(
    private val kobwebConfig: KobwebConfig,
    private val markdownConfig: MarkdownConfig,
) : DefaultTask() {
    private val markdownComponents = (markdownConfig as ExtensionAware).extensions.getByName("components") as MarkdownComponents
    private val markdownFeatures = (markdownConfig as ExtensionAware).extensions.getByName("features") as MarkdownFeatures

    private fun getMarkdownRoots(): Sequence<File> = project.getResourceRoots(TargetPlatform.JS)
        .map { root -> File(root, markdownConfig.markdownPath.get()) }

    @InputFiles
    fun getMarkdownFiles(): List<File> {
        val potentialMarkdownRoots = getMarkdownRoots()

        return project.getResourceFiles(TargetPlatform.JS)
            .filter { file -> file.isFile && file.extension == "md" }
            .filter { file -> potentialMarkdownRoots.any { root -> file.startsWith(root) } }
            .toList()
    }

    @OutputDirectory
    fun getGenDir(): File =
        kobwebConfig.getGenSrcRoot(project).resolve(kobwebConfig.getPagesPackage(project).replace(".", "/"))

    @TaskAction
    fun execute() {
        for (mdFile in getMarkdownFiles()) {
            for (root in getMarkdownRoots()) {
                if (mdFile.startsWith(root)) {
                    val mdFileRel = mdFile.relativeTo(root)

                    val extensions = mutableListOf<Extension>()
                    markdownFeatures.run {
                        if (autolink.get()) {
                            extensions.add(AutolinkExtension.create())
                        }
                        if (tables.get()) {
                            extensions.add(TablesExtension.create())
                        }
                        if (frontMatter.get()) {
                            extensions.add(YamlFrontMatterExtension.create())
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
                        kobwebConfig.getPagesPackage(project) + if (dirParts.isNotEmpty()) ".${dirParts.joinToString(".")}" else ""
                    val funName = mdFileRel.nameWithoutExtension.suffixIfNot("Page")

                    File(getGenDir(), "${dirParts.joinToString("/")}/$funName.kt").let { outputFile ->
                        outputFile.parentFile.mkdirs()

                        val ktRenderer = KotlinRenderer(markdownComponents, mdPackage, funName)
                        outputFile.writeText(ktRenderer.render(parser.parse(mdFile.readText())))
                    }
                }
            }
        }
    }
}