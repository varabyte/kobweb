package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.tasks.KobwebProcessSourcesTask
import com.varabyte.kobwebx.gradle.markdown.tasks.ConvertMarkdownTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebxMarkdownPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val kobwebBlock = project.extensions.findByName("kobweb") as? KobwebBlock
            ?: throw GradleException("The Gradle markdown plugin should only be applied AFTER com.varabyte.kobweb.application OR com.varabyte.kobweb.application plugins.")

        val markdownConfig = (kobwebBlock as ExtensionAware).extensions.create("markdown", MarkdownConfig::class.java)
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
            project.tasks.withType(KobwebProcessSourcesTask::class.java) {
                dependsOn(convertTask)
            }

            project.tasks.findByName(project.jsTarget.compileKotlin)?.dependsOn(convertTask)
        }
    }
}