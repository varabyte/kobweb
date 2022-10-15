package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.application.extensions.KobwebBlock
import com.varabyte.kobwebx.gradle.markdown.tasks.ConvertMarkdownTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.get

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebxMarkdownPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val kobwebBlock = project.extensions["kobweb"] as? KobwebBlock
            ?: throw GradleException("This plugin should only be applied AFTER the kobweb plugin")
        val kobwebxBlock = (project.extensions["kobwebx"] as ExtensionAware)

        val markdownConfig = kobwebxBlock.extensions.create("markdown", MarkdownConfig::class.java)
        (markdownConfig as ExtensionAware).extensions.apply {
            create("components", MarkdownComponents::class.java, project)
            create("features", MarkdownFeatures::class.java)
        }

        val convertTask = project.tasks.register(
            "kobwebxMarkdownConvert",
            ConvertMarkdownTask::class.java,
            kobwebBlock,
            markdownConfig
        )

        project.afterEvaluate {
            project.tasks.named("kobwebGenSiteSource") {
                dependsOn(convertTask)
            }
        }
    }
}