package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.buildTargets
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobweb.gradle.core.util.getResourceSources
import com.varabyte.kobwebx.gradle.markdown.tasks.ConvertMarkdownTask
import com.varabyte.kobwebx.gradle.markdown.tasks.ProcessMarkdownTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
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

        val processTask = project.tasks
            .register<ProcessMarkdownTask>("kobwebxMarkdownProcess", markdownBlock)

        val convertTask = project.tasks
            .register<ConvertMarkdownTask>("kobwebxMarkdownConvert", markdownBlock)

        project.buildTargets.withType<KotlinJsIrTarget>().configureEach {
            val jsTarget = JsTarget(this)

            processTask.configure {
                resources.set(project.getResourceSources(jsTarget))
            }
            convertTask.configure {
                resources.set(project.getResourceSources(jsTarget))
                generatedMarkdownDir.set(processTask.map { it.getGenResDir().get() })
                pagesPackage.set(kobwebBlock.pagesPackage)
            }

            project.kotlin.sourceSets.named(jsTarget.mainSourceSet) {
                kotlin.srcDir(convertTask)
                kotlin.srcDir(processTask.map { it.getGenSrcDir() })
            }
        }

        // Handle deprecation warnings
        @Suppress("DEPRECATION")
        project.afterEvaluate {
            if (markdownBlock.routeOverride.isPresent) {
                project.logger.warn(
                    "w: The markdown `markdown.routeOverride` property is slated for removal. It was introduced to support kebab-case URLs, but Kobweb has moved to defaulting to them instead."
                )
            }

            val markdownHandlers = markdownBlock.extensions.getByType<MarkdownHandlers>()
            if (markdownHandlers.defaultRoot.isPresent) {
                project.logger.warn(
                    "w: The `markdown.handlers.defaultRoot` property has moved to the parent markdown block instead, and this property is slated for removal. Use `markdown { defaultRoot.set(...) }` instead."
                )
            }
        }
    }
}
