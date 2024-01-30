package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.buildTargets
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobwebx.gradle.markdown.tasks.ConvertMarkdownTask
import com.varabyte.kobwebx.gradle.markdown.tasks.ProcessMarkdownTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget

@Suppress("unused") // KobwebApplicationPlugin is found by Gradle via reflection
class KobwebxMarkdownPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val kobwebBlock = project.extensions.findByName("kobweb") as? KobwebBlock
            ?: throw GradleException("The Gradle markdown plugin should only be applied AFTER com.varabyte.kobweb.application OR com.varabyte.kobweb.library plugins.")

        val markdownBlock = kobwebBlock.extensions.create<MarkdownBlock>("markdown", kobwebBlock.baseGenDir)
        markdownBlock.extensions.apply {
            create<MarkdownHandlers>("handlers", project)
            create<MarkdownFeatures>("features")
        }

        val convertTask = project.tasks
            .register<ConvertMarkdownTask>("kobwebxMarkdownConvert", kobwebBlock, markdownBlock)

        val processTask = project.tasks
            .register<ProcessMarkdownTask>("kobwebxMarkdownDataProcess", kobwebBlock, markdownBlock)

        project.buildTargets.withType<KotlinJsIrTarget>().configureEach {
            val jsTarget = JsTarget(this)
            project.kotlin.sourceSets.named(jsTarget.mainSourceSet) {
                kotlin.srcDir(convertTask)
                kotlin.srcDir(processTask.map { it.getGenSrcDir() })
            }
        }
    }
}
