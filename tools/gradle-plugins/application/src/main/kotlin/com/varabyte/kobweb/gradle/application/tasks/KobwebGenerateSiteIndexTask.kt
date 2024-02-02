@file:Suppress("DEPRECATION") // Intentionally supporting legacy API around `LibraryIndexMetadata`

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.extensions.index
import com.varabyte.kobweb.gradle.application.templates.createIndexFile
import com.varabyte.kobweb.gradle.core.metadata.LibraryIndexMetadata
import com.varabyte.kobweb.gradle.core.metadata.LibraryMetadata
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_INDEX
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_LIBRARY
import com.varabyte.kobweb.project.conf.KobwebConf
import kotlinx.html.link
import kotlinx.html.unsafe
import kotlinx.serialization.json.Json
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
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
    appBlock: AppBlock,
) : KobwebGenerateTask(appBlock, "Generate an index.html file for this Kobweb project") {
    @get:Input
    val indexBlock = appBlock.index

    @get:Input
    abstract val hasFaIconsDependency: Property<Boolean>

    @get:Input
    abstract val hasMdiIconsDependency: Property<Boolean>

    @get:InputFiles
    abstract val compileClasspath: ConfigurableFileCollection

    @OutputFile // needs to be dir to be registered as a kotlin srcDir
    fun getGenIndexFile(): Provider<RegularFile> = appBlock.getGenJsResRoot().map { it.file("index.html") }

    @TaskAction
    fun execute() {
        // Collect all <head> elements together. These will almost always be defined only by the app, but libraries are
        // allowed to declare some as well. If they do, they will have embedded serialized html in their library
        // artifacts.
        val headInitializers = indexBlock.head.get().toMutableList()

        if (hasFaIconsDependency.get()) {
            headInitializers.add {
                link {
                    rel = "stylesheet"
                    href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css"
                }
            }
        }

        if (hasFaIconsDependency.get()) {
            headInitializers.add {
                link {
                    rel = "stylesheet"
                    href =
                        "https://fonts.googleapis.com/css2?family=Material+Icons&family=Material+Icons+Outlined&family=Material+Icons+Two+Tone&family=Material+Icons+Round&family=Material+Icons+Sharp"
                }
            }
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

            libraryMetadata?.index?.headElements?.let { headElementsStr ->
                println("\tWE GOT $headElementsStr")
                val document = Jsoup.parse(headElementsStr)
                val headElements = document.head().children()

                if (headElements.isNotEmpty()) {
                    val optedOut = indexBlock.excludeTags.orNull
                        ?.invoke(AppBlock.IndexBlock.ExcludeTagsContext(file.name))
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

        val routePrefix = RoutePrefix(confInputs.routePrefix)
        getGenIndexFile().get().asFile.writeText(
            createIndexFile(
                confInputs.title,
                headInitializers,
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
