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
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.util.prefixIfNot
import org.jsoup.Jsoup
import java.io.File
import javax.inject.Inject

abstract class KobwebGenerateSiteIndexTask @Inject constructor(
    private val kobwebConf: KobwebConf,
    config: KobwebBlock,
    @get:Input val buildTarget: BuildTarget
) : KobwebGenerateTask(config, "Generate an index.html file for this Kobweb project") {

    // No one should define their own root `public/index.html` files anywhere in their resources.
    @InputFiles
    fun getUserDefinedRootIndexFiles() =
        getResourceFilesJsWithRoots()
            .mapNotNull { rootAndFile ->
                rootAndFile
                    .file
                    .takeIf { it.absolutePath != getGenIndexFile().absolutePath }
                    ?.takeIf { !it.isDescendantOf(project.layout.buildDirectory.asFile.get()) && rootAndFile.relativeFile.invariantSeparatorsPath == "public/index.html" }
            }.toList()

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

        getUserDefinedRootIndexFiles()
            .takeIf { it.isNotEmpty() }
            ?.let { externalIndexFiles ->
                throw GradleException("You are not supposed to define the root index file yourself. Kobweb provides its own. Use the `kobweb.app.index { ... }` block if you need to modify the generated index file. Problematic file(s): ${externalIndexFiles.joinToString(", ") { it.absoluteFile.toRelativeString(project.rootDir) }}")
            }

        // Collect all <head> elements together. These will almost always be defined only by the app, but libraries are
        // allowed to declare some as well. If they do, they will have embedded serialized html in their library
        // artifacts.
        val headInitializers = kobwebBlock.app.index.head.get().toMutableList()
        val kobwebLibraries = mutableListOf<File>()
        getCompileClasspath().get().files.forEach { file ->
            file.searchZipFor(KOBWEB_METADATA_INDEX) { bytes ->
                kobwebLibraries.add(file)

                val indexMetadata = Json.decodeFromString<LibraryIndexMetadata>(bytes.decodeToString())
                val document = Jsoup.parse(indexMetadata.headElements)
                val headElements = document.head().children()

                if (headElements.isNotEmpty()) {
                    val optedOut =
                        kobwebBlock.app.index.excludeTags.orNull?.invoke(AppBlock.IndexBlock.ExcludeTagsContext(file.name))
                            ?: false

                    if (!optedOut) {
                        logger.warn(buildString {
                            appendLine()
                            appendLine("Dependency artifact \"${file.name}\" will add the following <head> elements to your site's index.html:")
                            appendLine(headElements.joinToString("\n") { "   " + it.outerHtml() })
                            append(
                                "Add `kobweb { app { index { excludeTagsForDependency(\"${
                                    file.nameWithoutExtension.substringBeforeLast("-js-")
                                }\") } } }` to your build.gradle.kts file to opt-out."
                            )
                        })

                        headInitializers.add {
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
                    } else {
                        logger.warn(buildString {
                            appendLine()
                            appendLine("Dependency artifact \"${file.name}\" was prevented from adding the following <head> elements to your site's index.html:")
                            append(headElements.joinToString("\n") { "   " + it.outerHtml() })
                        })
                    }
                }
            }
        }

        val routePrefix = RoutePrefix(kobwebConf.site.routePrefix)
        getGenIndexFile().writeText(
            createIndexFile(
                kobwebConf.site.title,
                headInitializers,
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
