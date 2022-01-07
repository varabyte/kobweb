package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.get

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebxMarkdownPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val kobwebConfig = project.extensions["kobweb"] as? KobwebConfig
            ?: throw GradleException("This plugin should only be applied AFTER the kobweb plugin")
        val kobwebxBlock = (project.extensions["kobwebx"] as ExtensionAware)

        val markdownConfig = kobwebxBlock.extensions.create("markdown", MarkdownConfig::class.java)
        val markdownComponents = (markdownConfig as ExtensionAware).extensions.create("components", MarkdownComponents::class.java)
        (markdownConfig as ExtensionAware).extensions.create("features", MarkdownFeatures::class.java)

        val convertTask = project.tasks.register(
            "kobwebxMarkdownConvert",
            ConvertMarkdownTask::class.java,
            kobwebConfig,
            markdownConfig
        )

        project.afterEvaluate {
            markdownComponents.useSilk.convention(project.configurations.asSequence()
                .flatMap { config -> config.dependencies }
                .any { dependency -> dependency.name == "kobweb-silk" }
            )

            project.tasks.named("kobwebGenSite") {
                dependsOn(convertTask)
            }
        }
    }
}