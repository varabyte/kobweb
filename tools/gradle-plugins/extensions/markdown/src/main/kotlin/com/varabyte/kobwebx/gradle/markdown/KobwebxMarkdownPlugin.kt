package com.varabyte.kobwebx.gradle.markdown

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.buildTargets
import com.varabyte.kobweb.gradle.core.kmp.kotlin
import com.varabyte.kobweb.gradle.core.util.getJsDependencyResults
import com.varabyte.kobweb.gradle.core.util.hasDependencyNamed
import com.varabyte.kobwebx.gradle.markdown.handlers.MarkdownHandlers
import com.varabyte.kobwebx.gradle.markdown.tasks.ConvertMarkdownTask
import com.varabyte.kobwebx.gradle.markdown.tasks.MarkdownTask
import com.varabyte.kobwebx.gradle.markdown.tasks.ProcessMarkdownTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
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

        val processTask = project.tasks
            .register<ProcessMarkdownTask>("kobwebxMarkdownProcess", markdownBlock)

        val convertTask = project.tasks
            .register<ConvertMarkdownTask>("kobwebxMarkdownConvert", markdownBlock)

        project.buildTargets.withType<KotlinJsIrTarget>().configureEach {
            val jsTarget = JsTarget(this)

            fun provideAllMarkdownDirs(): FileCollection {
                val projectResources = project.layout.projectDirectory.dir("src/${jsTarget.mainSourceSet}/resources/${markdownBlock.markdownPath.get()}")

                return project.files()
                    .from(projectResources)
                    .from(markdownBlock.additionalDirectories)
            }

            // Configures both ProcessMarkdownTask & ConvertMarkdownTask
            project.tasks.withType<MarkdownTask>().configureEach {
                pagesPackage.set(kobwebBlock.pagesPackage)
                projectGroup.set(project.group)
                markdownDirs.from(provideAllMarkdownDirs())
            }

            convertTask.configure {
                dependsOnMarkdownArtifact.set(
                    project.getJsDependencyResults().hasDependencyNamed("com.varabyte.kobwebx:kobwebx-markdown")
                )

                markdownDirs.from(
                    project.files()
                        .from(provideAllMarkdownDirs())
                        .from(processTask.map { task ->
                            task.getGenResDir().map { it.dir(markdownBlock.markdownPath.get()) }
                        })
                )
            }

            project.kotlin.sourceSets.named(jsTarget.mainSourceSet) {
                kotlin.srcDir(convertTask)
                kotlin.srcDir(processTask.map { it.getGenSrcDir() })
                resources.srcDir(processTask.map { it.getGenResDir() })
            }
        }
    }
}
