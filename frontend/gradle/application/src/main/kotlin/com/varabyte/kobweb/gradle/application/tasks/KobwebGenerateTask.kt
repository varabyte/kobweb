@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.templates.createHtmlFile
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import com.varabyte.kobweb.project.conf.KobwebConfFile
import org.gradle.api.GradleException
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class KobwebGenerateTask @Inject constructor(config: KobwebConfig)
    : KobwebProjectTask(config, "Generate Kobweb code and resources") {

    @OutputDirectory
    fun getGenDir(): File = project.layout.buildDirectory.dir(config.genDir.get()).get().asFile

    @TaskAction
    fun execute() {
        val conf = with(KobwebConfFile(kobwebProject.kobwebFolder)) {
            content ?: throw GradleException("Could not find configuration file: ${this.path}")
        }

        val genDirSrcRoot = config.getGenSrcRoot(project)
        val genDirResRoot = config.getGenResRoot(project)

        with(kobwebProject.parseData(project.group.toString(), config.pagesPackage.get(), getSourceFiles())) {
            File(genDirSrcRoot, "main.kt").writeText(
                createMainFunction(
                    app?.fqcn,
                    // Sort by route as it makes the generated registration logic easier to follow
                    pages
                        .associate { it.fqcn to it.route }
                        .toList()
                        .sortedBy { (_, route) -> route }
                        .toMap()
                )
            )
        }

        File(genDirResRoot, getPublicPath()).let { publicRoot ->
            publicRoot.mkdirs()
            File(publicRoot, "index.html").writeText(
                createHtmlFile(
                    conf.site.title,
                    // TODO(Bug #7): Only specify font-awesome link if necessary
                    listOf("""<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" />"""),
                    conf.server.files.dev.script.substringAfterLast("/")
                )
            )
        }
    }
}