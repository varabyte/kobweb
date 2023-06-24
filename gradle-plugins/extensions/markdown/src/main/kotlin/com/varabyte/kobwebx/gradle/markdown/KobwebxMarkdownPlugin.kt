package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.buildTargets
import com.varabyte.kobweb.gradle.core.tasks.KobwebProcessSourcesTask
import com.varabyte.kobweb.gradle.core.util.namedOrNull
import com.varabyte.kobwebx.gradle.markdown.tasks.ConvertMarkdownTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebxMarkdownPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val kobwebBlock = project.extensions.findByName("kobweb") as? KobwebBlock
            ?: throw GradleException("The Gradle markdown plugin should only be applied AFTER com.varabyte.kobweb.application OR com.varabyte.kobweb.library plugins.")

        val markdownConfig = (kobwebBlock as ExtensionAware).extensions.create<MarkdownConfig>("markdown")
        (markdownConfig as ExtensionAware).extensions.apply {
            create<MarkdownHandlers>("handlers", project)
            create<MarkdownFeatures>("features")
        }

        val convertTask = project.tasks.register(
            "kobwebxMarkdownConvert",
            ConvertMarkdownTask::class.java,
            kobwebBlock,
            markdownConfig
        )

        project.tasks.withType<KobwebProcessSourcesTask>().configureEach {
            dependsOn(convertTask)
        }
        project.buildTargets.withType<KotlinJsIrTarget>().configureEach {
            val jsTarget = JsTarget(this)
            project.tasks.namedOrNull(jsTarget.compileKotlin)?.configure { dependsOn(convertTask) }
        }
    }
}
