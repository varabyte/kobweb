@file:Suppress("DEPRECATION") // Intentionally supporting legacy API around `LibraryIndexMetadata`

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.extensions.app
import com.varabyte.kobweb.gradle.application.extensions.serializeHeadContents
import com.varabyte.kobweb.gradle.application.templates.createIndexFile
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.metadata.LibraryIndexMetadata
import com.varabyte.kobweb.gradle.core.metadata.LibraryMetadata
import com.varabyte.kobweb.gradle.core.util.hasTransitiveJsDependencyNamed
import com.varabyte.kobweb.gradle.core.util.isDescendantOf
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_INDEX
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_LIBRARY
import com.varabyte.kobweb.project.conf.KobwebConf
import kotlinx.html.dom.append
import kotlinx.html.dom.document
import kotlinx.html.dom.serialize
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.unsafe
import kotlinx.serialization.json.Json
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.util.prefixIfNot
import org.jsoup.Jsoup
import javax.inject.Inject

class KobwebGenIndexConfInputs(
    @get:Input val title: String,
    @get:Input val routePrefix: String,
    @get:Input val script: String,
) {
    constructor(kobwebConf: KobwebConf) : this(
        title = kobwebConf.site.title,
        routePrefix = kobwebConf.site.routePrefix,
        script = kobwebConf.server.files.dev.script
    )
}

abstract class KobwebGenerateSiteIndexTask @Inject constructor(
    @get:Nested val confInputs: KobwebGenIndexConfInputs,
    @get:Input val buildTarget: BuildTarget,
    block: KobwebBlock,
    @get:Nested val indexBlock: AppBlock.IndexBlock
) : KobwebGenerateTask(block, "Generate an index.html file for this Kobweb project") {

    // No one should define their own root `public/index.html` files anywhere in their resources.
    @InputFiles
    fun getUserDefinedRootIndexFiles() =
        getResourceFilesJsWithRoots()
            .mapNotNull { rootAndFile ->
                rootAndFile.takeIf {
                    // Ignore files that are in our build directory, since we put them there. We are looking for index
                    // files explicitly added by a user, often because they don't realize that Kobweb generates one yet.
                    !it.file.isDescendantOf(projectLayout.buildDirectory.asFile.get())
                        && it.relativeFile.invariantSeparatorsPath == "public/index.html"
                }
            }
            .map { it.file }
            .toList()

    @get:InputFiles
    abstract val compileClasspath: ConfigurableFileCollection

    @OutputFile
    fun getGenIndexFile(): Provider<RegularFile> = kobwebBlock.app.getGenJsResRoot().map { it.file("index.html") }

    @TaskAction
    fun execute() {
        // Collect all <head> elements together. These will almost always be defined only by the app, but libraries are
        // allowed to declare some as well. If they do, they will have embedded serialized html in their library
        // artifacts.
        val headElements = indexBlock.newHead.get().toMutableList().apply {
            addAll(indexBlock.head.get().map { block -> serializeHeadContents(block) })
        }

        if (project.hasTransitiveJsDependencyNamed("silk-icons-fa")) {
            headElements.add(serializeHeadContents {
                link {
                    rel = "stylesheet"
                    href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"
                }
            })
        }

        if (project.hasTransitiveJsDependencyNamed("silk-icons-mdi")) {
            headElements.add(serializeHeadContents {
                link {
                    rel = "stylesheet"
                    href =
                        "https://fonts.googleapis.com/css2?family=Material+Icons&family=Material+Icons+Outlined&family=Material+Icons+Two+Tone&family=Material+Icons+Round&family=Material+Icons+Sharp"
                }
            })
        }

        getUserDefinedRootIndexFiles()
            .takeIf { it.isNotEmpty() }
            ?.let { externalIndexFiles ->
                throw GradleException(
                    "You are not supposed to define the root index file yourself. Kobweb provides its own. Use the `kobweb.app.index { ... }` block if you need to modify the generated index file. Problematic file(s): ${
                        externalIndexFiles.joinToString(
                            ", "
                        ) { it.absoluteFile.toRelativeString(projectLayout.projectDirectory.asFile) }
                    }"
                )
            }

        compileClasspath.forEach { file ->
            var libraryMetadata: LibraryMetadata? = null

            file.searchZipFor(KOBWEB_METADATA_LIBRARY) { bytes ->
                libraryMetadata = Json.decodeFromString(LibraryMetadata.serializer(), bytes.decodeToString())
            }

            if (libraryMetadata == null) {
                // It's possible that we're loading a library that was built with an older version of Kobweb that used
                // the index.json file instead of library.json. If so, we'll try to load it from there.
                @Suppress("DEPRECATION")
                file.searchZipFor(KOBWEB_METADATA_INDEX) { bytes ->
                    libraryMetadata = Json.decodeFromString(LibraryIndexMetadata.serializer(), bytes.decodeToString())
                        .toLibraryMetadata()
                }
            }

            libraryMetadata?.index?.headElements?.let { headElementsData ->
                // Support legacy library metadata that include the <head> tag itself
                val headElementsStr = if (headElementsData.startsWith("<head")) {
                    val document = Jsoup.parse(headElementsData)
                    val elements = document.head().children()
                    serializeHeadContents {
                        elements.forEach { element ->
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
                } else headElementsData

                if (headElementsStr.isBlank()) {
                    return@forEach
                }
                // There doesn't seem to be a better way to pretty print the <head> contents using kotlinx.html
                val headPrettyPrint = document {
                    append.head { unsafe { raw(headElementsStr) } }
                }.serialize().lines().drop(3).dropLast(2).joinToString("\n").trimIndent()

                val optedOut = indexBlock.excludeHtmlForDependencies.get().any { file.name.startsWith(it) }
                    || indexBlock.excludeTags.orNull?.invoke(AppBlock.IndexBlock.ExcludeTagsContext(file.name)) ?: false

                if (!optedOut) {
                    if (indexBlock.suppressHtmlWarningsForDependencies.get()
                            .none { file.name.startsWith(it) }
                    ) {
                        val dep = file.nameWithoutExtension.substringBeforeLast("-js-")
                        logger.warn(buildString {
                            appendLine()
                            appendLine("Dependency artifact \"${file.name}\" will add the following <head> elements to your site's index.html:")
                            appendLine(headPrettyPrint)
                            appendLine("You likely want to let them do this, as it is probably necessary for the library's functionality, but you should still audit what they're doing.")
                            append(
                                "Add `kobweb { app { index { excludeHtmlForDependencies.add(\"$dep\") } } }` to your build.gradle.kts file to reject these elements (or `suppressHtmlWarningsForDependencies.add(\"$dep\")` to hide this message)."
                            )
                        })
                    }
                    headElements.add(headElementsStr)
                } else {
                    logger.warn(buildString {
                        appendLine()
                        appendLine("Dependency artifact \"${file.name}\" was prevented from adding the following <head> elements to your site's index.html:")
                        append(headPrettyPrint)
                    })
                }
            }
        }

        val routePrefix = RoutePrefix(confInputs.routePrefix)
        getGenIndexFile().get().asFile.writeText(
            createIndexFile(
                confInputs.title,
                indexBlock.lang.get(),
                headElements,
                // Our script will always exist at the root folder, so be sure to ground it,
                // e.g. "example.js" -> "/example.js", so the root will be searched even if we're visiting a page in
                // a subdirectory.
                routePrefix.prependTo(confInputs.script.substringAfterLast("/").prefixIfNot("/")),
                indexBlock.scriptAttributes.get(),
                buildTarget
            )
        )
    }
}
