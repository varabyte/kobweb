@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.KobwebConfig
import com.varabyte.kobweb.gradle.application.extensions.TargetPlatform
import com.varabyte.kobweb.gradle.application.extensions.getResourceFilesWithRoots
import com.varabyte.kobweb.gradle.application.extensions.getSourceFiles
import com.varabyte.kobweb.gradle.application.templates.createHtmlFile
import com.varabyte.kobweb.gradle.application.templates.createMainFunction
import com.varabyte.kobweb.project.KobwebProject
import com.varabyte.kobweb.project.conf.KobwebConfFile
import org.gradle.api.GradleException
import org.gradle.api.tasks.*
import java.io.File
import javax.inject.Inject

abstract class KobwebGenerateTask @Inject constructor(private val config: KobwebConfig) : KobwebTask("Generate Kobweb code and resources") {
    private val kobwebProject = KobwebProject()

    @InputFile
    fun getConfFile(): File = project.layout.projectDirectory.file(kobwebProject.kobwebFolder.resolve("conf.yaml").toString()).asFile

    @OutputDirectory
    fun getGenDir(): File = project.layout.buildDirectory.dir(config.genDir.get()).get().asFile

    @InputFiles
    fun getSourceFiles(): List<File> = project.getSourceFiles(TargetPlatform.JS).toList()

    @InputFiles
    fun getResourceFiles(): List<File> = project.getResourceFilesWithRoots(TargetPlatform.JS)
        .filter { rootAndFile -> rootAndFile.relativeFile.path.startsWith("${getPublicPath()}/") }
        .map { it.file }
        .toList()

    /**
     * The root package of all pages.
     *
     * Any composable function not under this root will be ignored, even if annotated by @Page.
     *
     * An initial '.' means this should be prefixed by the project group, e.g. ".pages" -> "com.example.pages"
     */
    @Input
    fun getPagesPackage(): String = config.pagesPackage.get()

    /**
     * The path of public resources inside the project's resources folder, e.g. "public" ->
     * "src/jsMain/resources/public"
     */
    @Input
    fun getPublicPath(): String = config.publicPath.get()

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