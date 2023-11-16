package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.extensions.app
import com.varabyte.kobweb.gradle.application.extensions.index
import com.varabyte.kobweb.gradle.application.templates.createIndexFile
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.metadata.LibraryIndexMetadata
import com.varabyte.kobweb.gradle.core.util.hasTransitiveJsDependencyNamed
import com.varabyte.kobweb.gradle.core.util.isDescendantOf
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_INDEX
import com.varabyte.kobweb.project.conf.KobwebConf
import kotlinx.html.link
import kotlinx.html.unsafe
import kotlinx.serialization.json.Json
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.util.prefixIfNot
import org.jsoup.Jsoup
import javax.inject.Inject

abstract class KobwebGenerateSiteIndexTask @Inject constructor(
    private val kobwebConf: KobwebConf,
    config: KobwebBlock,
    @get:Input val buildTarget: BuildTarget
) : KobwebGenerateTask(config, "Generate an index.html file for this Kobweb project") {

    @InputFiles
    fun getResourceFiles() = run {
        // Don't let stuff we output force ourselves to run again
        val genIndexFile = getGenIndexFile()
        getResourceFilesJs()
            .filter { it.absolutePath != genIndexFile.absolutePath }
    }

    @InputFiles
    fun getCompileClasspath() = project.configurations.named(project.jsTarget.compileClasspath)

    @OutputFile
    fun getGenIndexFile() = kobwebBlock.getGenJsResRoot<AppBlock>(project).resolve("index.html")

    @TaskAction
    fun execute() {
        if (project.hasTransitiveJsDependencyNamed("silk-icons-fa")) {
            kobwebBlock.app.index.head.add {
                link {
                    rel = "stylesheet"
                    href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css"
                }
            }
        }

        if (project.hasTransitiveJsDependencyNamed("silk-icons-mdi")) {
            kobwebBlock.app.index.head.add {
                link {
                    rel = "stylesheet"
                    href =
                        "https://fonts.googleapis.com/css2?family=Material+Icons&family=Material+Icons+Outlined&family=Material+Icons+Two+Tone&family=Material+Icons+Round&family=Material+Icons+Sharp"
                }
            }
        }

        getResourceFilesJsWithRoots()
            .mapNotNull { rootAndFile -> rootAndFile.file.takeIf { !it.isDescendantOf(project.layout.buildDirectory.asFile.get()) && rootAndFile.relativeFile.invariantSeparatorsPath == "public/index.html" } }
            .singleOrNull()
            ?.let { indexFile ->
                logger.error("$indexFile: You are not supposed to define this file yourself. Kobweb provides its own. Use the kobweb.index { ... } block if you need to modify the generated index file.")
            }

        // Collect all <head> elements together. These will almost always be defined only by the app, but libraries are
        // allowed to declare some as well. If they do, they will have embedded serialized html in their library
        // artifacts.
        val headElements = kobwebBlock.app.index.head.get().toMutableList()
        getCompileClasspath().get().files.forEach { file ->
            file.searchZipFor(KOBWEB_METADATA_INDEX) { bytes ->
                val indexMetadata = Json.decodeFromString<LibraryIndexMetadata>(bytes.decodeToString())
                val document = Jsoup.parse(indexMetadata.headElements)
                headElements.add {
                    document.head().children().forEach { element ->
                        // Weird hack alert -- void elements (like <link>, <meta>), which are common in <head> tags, are
                        // considered by JSoup as self-closing even without a trailing slash. This is valid HTML but
                        // currently kotlinx html can't seem to handle them when specified as raw text, triggering a
                        // parse error. (See also: https://github.com/Kotlin/kotlinx.html/issues/247). To work around
                        // this limitation, we force a trailing slash ourselves.
                        unsafe {
                            val rawElement = element.outerHtml()
                            if (element.tag().isSelfClosing && !rawElement.endsWith("/>")) {
                                raw(rawElement.removeSuffix(">") + "/>")
                            } else {
                                raw(rawElement)
                            }
                        }
                    }
                }
            }
        }


        val routePrefix = RoutePrefix(kobwebConf.site.routePrefix)
        getGenIndexFile().writeText(
            createIndexFile(
                kobwebConf.site.title,
                headElements,
                // Our script will always exist at the root folder, so be sure to ground it,
                // e.g. "example.js" -> "/example.js", so the root will be searched even if we're visiting a page in
                // a subdirectory.
                routePrefix.prependTo(kobwebConf.server.files.dev.script.substringAfterLast("/").prefixIfNot("/")),
                kobwebBlock.app.index.scriptAttributes.get(),
                buildTarget
            )
        )
    }
}
