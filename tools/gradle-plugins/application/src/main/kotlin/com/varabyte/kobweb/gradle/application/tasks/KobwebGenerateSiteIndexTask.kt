package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.common.navigation.BasePath
import com.varabyte.kobweb.gradle.application.BuildTarget
import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.extensions.index
import com.varabyte.kobweb.gradle.application.extensions.interceptUrls
import com.varabyte.kobweb.gradle.application.templates.createIndexFile
import com.varabyte.kobweb.gradle.core.metadata.LibraryMetadata
import com.varabyte.kobweb.gradle.core.util.HtmlUtil
import com.varabyte.kobweb.gradle.core.util.downloadOrCached
import com.varabyte.kobweb.gradle.core.util.hasDependencyNamed
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_LIBRARY
import com.varabyte.kobweb.project.conf.KobwebConf
import kotlinx.html.dom.append
import kotlinx.html.dom.document
import kotlinx.html.dom.serialize
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.unsafe
import kotlinx.serialization.json.Json
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.util.prefixIfNot
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import java.net.URI
import javax.inject.Inject
import kotlin.math.min

class KobwebGenIndexConfInputs(
    @get:Input val title: String,
    @get:Input val basePath: String,
    @get:Input val script: String,
) {
    constructor(kobwebConf: KobwebConf) : this(
        title = kobwebConf.site.title,
        basePath = kobwebConf.site.basePath,
        script = kobwebConf.server.files.dev.script
    )
}

abstract class KobwebGenerateSiteIndexTask @Inject constructor(
    private val appBlock: AppBlock,
    @get:Nested val confInputs: KobwebGenIndexConfInputs,
    @get:Input val buildTarget: BuildTarget,
) : KobwebGenerateTask("Generate an index.html file for this Kobweb project") {
    @get:Nested
    val indexBlock: AppBlock.IndexBlock = appBlock.index

    @get:Nested
    val interceptUrls = appBlock.index.interceptUrls

    @get:Input
    abstract val publicPath: Property<String>

    @get:InputFiles
    abstract val compileClasspath: ConfigurableFileCollection

    @get:Internal
    abstract val dependencies: ListProperty<ResolvedDependencyResult>

    @get:Input
    val hasFaDependency: Provider<Boolean>
        get() = dependencies.hasDependencyNamed("com.varabyte.kobwebx:silk-icons-fa")

    @get:Input
    val hasMdiDependency: Provider<Boolean>
        get() = dependencies.hasDependencyNamed("com.varabyte.kobwebx:silk-icons-mdi")

    private val httpProtocolRegex = Regex("""^https?://""")
    // matches `url(https://example.com)`, `url('https://example.com')`, and `url("https://example.com")`
    // returning the juicy URL center as a match (without the outer quotes or parens)
    private val urlRegex = Regex("""url\(["']?([^)"']+)["']?\)""")

    @OutputFile
    fun getGenIndexFile(): Provider<RegularFile> = appBlock.getGenJsResRoot().map { it.file("index.html") }

    @OutputDirectory
    fun getGenResDir() = appBlock.getGenJsResRoot("self-host")

    private fun getGenPublicRoot() = getGenResDir().get().asFile.resolve(publicPath.get())

    private fun makeLocalCopyOfUrl(url: String, basePath: BasePath): String {
        fun File.resolveWithMkdirs(name: String): File {
            return resolve(name).also { it.parentFile.mkdirs() }
        }

        val downloadResult = projectLayout.downloadOrCached(logger, URI(url).toURL())
        val selfHostPrefix = "_kobweb/self-host"
        val selfHostRoot = getGenPublicRoot().resolve(selfHostPrefix)

        var urlAsPath = url.substringAfterLast("://").substringBefore("?").substringBefore("#")

        // Slight hack: detecting CSS files is really important for self-hosting (since we search css files for internal
        // links that we also need to self-host), but in at least one case we've seen a URL that is a CSS file but had
        // no extension. Normally, when you download a file, its content type is included in the response, but we lose
        // that information when saving the file to disk. Appending an explicit css extension works around that.
        if (
            !urlAsPath.substringAfterLast("/").contains(".")
            && downloadResult.metadata.contentType?.contains("text/css") == true
        ) {
            urlAsPath += ".css"
        }

        val selfHostedFile = selfHostRoot.resolveWithMkdirs(urlAsPath).apply { writeBytes(downloadResult.file.readBytes()) }

        if (urlAsPath.endsWith(".css")) {
            val originalText = selfHostedFile.readText()

            selfHostedFile.writeText(buildString {
                append(originalText)

                // Run through the original text and replace all url(...) values with their local copy paths.
                // We use `fromIndex` to avoid replacing the same url(...) value multiple times.
                var fromIndex = 0
                urlRegex
                    .findAll(originalText)
                    .map { it.groupValues[1] }
                    .forEach { urlValue ->
                        val finalUrl = when {
                            httpProtocolRegex.containsMatchIn(urlValue) -> urlValue
                            urlValue.contains(":") -> null // ignore other protocols, e.g. `data:`
                            else -> {
                                // If here, the URL value is relative, e.g. ("ex", "./ex", "../ex").
                                val paramsIndex = min(url.indexOf("?"), url.indexOf("#"))
                                val urlBase = url.substringBeforeLast("/")
                                // urlValue.removePrefix("./") removes useless "." in the middle of a URL that seems to
                                // confuse the downloader.
                                // Good: `/url/base` + './nested/value` -> "/url/base/nested/value`
                                // Bad:  `/url/base` + './nested/value` -> "/url/base/./nested/value`
                                urlBase + "/" + urlValue.removePrefix("./") + if (paramsIndex >= 0) url.substring(paramsIndex) else ""
                            }
                        }?.let { makeLocalCopyOfUrl(it, basePath) }

                        if (finalUrl != null) {
                            indexOf(urlValue, fromIndex).takeIf { it >= 0 }?.let { i ->
                                // If the URL is a relative value (e.g. "../fonts/xyz.woff2"), then we don't need to
                                // replace it. We already downloaded a local file and placed it in the right place.
                                fromIndex = i + if (urlValue.startsWith(".")) {
                                    urlValue.length
                                } else {
                                    replace(i, i + urlValue.length, finalUrl)
                                    finalUrl.length
                                }
                            }
                        }
                    }
            })
        }

        return basePath.prependTo("/$selfHostPrefix/$urlAsPath")
    }

    private class ApplyUrlInterceptorsResult(
        val elements: List<String>,
        val changedUrls: Map<String, String?> = emptyMap()
    )

    private fun List<String>.applyUrlInterceptors(basePath: BasePath): ApplyUrlInterceptorsResult {
        val rejects = interceptUrls.rejects.get()
        val replacements = interceptUrls.replacements.get()
        val selfHosting = interceptUrls.selfHosting.get()

        // Early abort if we're sure we don't need to do any work (99.9% of users, probably!)
        if (rejects.isEmpty() && replacements.isEmpty() && !selfHosting.enabled) {
            return ApplyUrlInterceptorsResult(this)
        }

        val elements = this.flatMap { html ->
            Jsoup.parse(html).apply {
                outputSettings().syntax(Document.OutputSettings.Syntax.xml)
            }.selectFirst("head")?.children() ?: emptyList()
        }

        val changedUrls = mutableMapOf<String, String?>()

        // Replace the given URL with a new version, assuming it was detected that the URL should be modified. This
        // returns the element itself or null as a signal to indicate that the element should be removed from the final
        // head block as its link was rejected.
        fun Element.replaceUrl(url: String, action: Element.(String) -> Unit): Element? {
            if (rejects.contains(url)) {
                changedUrls[url] = null
                return null
            }

            var finalUrl: String? = null
            replacements[url]?.let { finalUrl = it }

            if (finalUrl == null &&
                selfHosting.enabled && !selfHosting.excludes.contains(url) &&
                replacements.values.none { it == url }
            ) {
                finalUrl = makeLocalCopyOfUrl(url, basePath)
            }

            @Suppress("NAME_SHADOWING")
            finalUrl?.let { finalUrl ->
                action(finalUrl)
                changedUrls[url] = finalUrl
            }

            return this
        }

        val filteredElements = elements.mapNotNull { element: Element ->
            when (element.tagName()) {
                "link" -> {
                    val rel = element.attr("rel")
                    val href = element.attr("href")

                    if (interceptUrls.linkRels.get().contains(rel) && httpProtocolRegex.containsMatchIn(href)) {
                        return@mapNotNull element.replaceUrl(href) { attributes().put("href", it) }
                    }
                }

                "script" -> {
                    val src = element.attr("src")

                    if (httpProtocolRegex.containsMatchIn(src)) {
                        return@mapNotNull element.replaceUrl(src) { attributes().put("src", it) }
                    }
                }

                "style" -> {
                    var content = element.data()
                    val originalContent = content
                    urlRegex.findAll(content).map { it.groupValues[1] }.forEach { urlValue ->
                        val shouldRejectElement = element.replaceUrl(urlValue) { content = content.replace(urlValue, it)} == null
                        if (shouldRejectElement) return@mapNotNull null
                    }

                    if (content != originalContent) {
                        element.html(content)
                    }
                }
            }

            element
        }

        val commonErrorMessage = "but it was not detected in your <head> elements. This could indicate a stale configuration that should be removed or updated to a new URL."
        rejects.forEach { reject ->
            if (!changedUrls.containsKey(reject)) {
                logger.warn("w: Your project is configured to intercept and reject URL \"$reject\", $commonErrorMessage")
            }
        }

        replacements.keys.forEach { toReplace ->
            if (!changedUrls.containsKey(toReplace)) {
                logger.warn("w: Your project is configured to intercept and replace URL \"$toReplace\", $commonErrorMessage")
            }
        }

        selfHosting.excludes.forEach { exclude ->
            if (!changedUrls.containsKey(exclude)) {
                logger.warn("w: Your self-hosting configuration excludes URL \"$exclude\", $commonErrorMessage")
            }
        }

        return ApplyUrlInterceptorsResult(
            filteredElements.map { it.toString() },
            changedUrls
        )
    }

    @TaskAction
    fun execute() {
        // Collect all <head> elements together. These will almost always be defined only by the app, but libraries are
        // allowed to declare some as well. If they do, they will have embedded serialized html in their library
        // artifacts.
        val headElements = mutableListOf(indexBlock.serializedHead.get())

        if (hasFaDependency.get()) {
            headElements.add(HtmlUtil.serializeHeadContents {
                link {
                    rel = "stylesheet"
                    href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"
                }
            })
        }

        if (hasMdiDependency.get()) {
            headElements.add(HtmlUtil.serializeHeadContents {
                link {
                    rel = "stylesheet"
                    href =
                        "https://fonts.googleapis.com/css2?family=Material+Icons&family=Material+Icons+Outlined&family=Material+Icons+Two+Tone&family=Material+Icons+Round&family=Material+Icons+Sharp"
                }
            })
        }

        val headElementsFromDependencies = mutableMapOf<File, List<String>>()
        compileClasspath.forEach { file ->
            var libraryMetadata: LibraryMetadata? = null

            file.searchZipFor(KOBWEB_METADATA_LIBRARY) { bytes ->
                libraryMetadata = Json.decodeFromString(LibraryMetadata.serializer(), bytes.decodeToString())
            }

            libraryMetadata?.index?.headElements?.let { headElementsStr ->
                if (headElementsStr.isBlank()) {
                    return@forEach
                }

                // There doesn't seem to be a better way to pretty print the <head> contents using kotlinx.html
                val headElementsList = document {
                    append.head { unsafe { raw(headElementsStr) } }
                }.serialize().lines().drop(3).dropLast(2).map { it.trim() }

                val optedOut = indexBlock.excludeHtmlForDependencies.get().any { file.name.startsWith(it) }
                if (!optedOut) {
                    headElementsFromDependencies[file] = headElementsList
                    headElements.add(headElementsStr)
                } else {
                    logger.info(buildString {
                        appendLine("Dependency artifact \"${file.name}\" was prevented (via `app.kobweb.index.excludeHtmlForDependencies`) from adding the following <head> elements to your site's index.html:")
                        headElementsList.forEach { appendLine("* \"$it\"") }
                    })
                }
            }
        }

        // Collect body elements
        // AFTER_SCRIPT (default): reuse existing `body` property for backward compatibility
        val bodyAfterScriptElements: List<String> = indexBlock.serializedBody.get().let {
            if (it.isBlank()) emptyList() else listOf(it)
        }
        // START and END positions are new
        val bodyStartElements: List<String> = indexBlock.serializedBodyStart.get().let {
            if (it.isBlank()) emptyList() else listOf(it)
        }
        val bodyEndElements: List<String> = indexBlock.serializedBodyEnd.get().let {
            if (it.isBlank()) emptyList() else listOf(it)
        }

        val basePath = BasePath(confInputs.basePath)
        getGenIndexFile().get().asFile.writeText(
            createIndexFile(
                title = confInputs.title,
                lang = indexBlock.lang.get(),
                headElements = headElements.applyUrlInterceptors(basePath).also { result ->
                    logger.lifecycle("Final <head> elements:")
                    logger.lifecycle("```")
                    result.elements.forEach { logger.lifecycle("  ${it.replace("\n", "")}") }
                    logger.lifecycle("```")

                    headElementsFromDependencies.forEach { (artifact, elements) ->
                        val dep = artifact.nameWithoutExtension.substringBeforeLast("-js-")
                        logger.lifecycle("* Requested by \"${artifact.name}:\"")
                        elements.forEach { element -> logger.lifecycle("  $element") }
                        logger.lifecycle("  ! Call `kobweb.app.index.excludeHtmlForDependencies.add(\"$dep\")` in your build.gradle.kts file to reject these elements.")
                    }
                    result.changedUrls.forEach { (from, to) ->
                        logger.lifecycle("* URL intercepted: \"$from\" -> ${to?.let { "\"$it\"" } ?: "(removed)"}")
                    }

                    if (result.changedUrls.isEmpty()) {
                        logger.lifecycle("NOTE: You can configure external <head> links using the `kobweb.app.index.interceptUrls` block.")
                    }
                }.elements,
                // Our script will always exist at the root folder, so be sure to ground it,
                // e.g. "example.js" -> "/example.js", so the root will be searched even if we're visiting a page in
                // a subdirectory.
                src = basePath.prependTo(confInputs.script.substringAfterLast("/").prefixIfNot("/")),
                scriptAttributes = indexBlock.scriptAttributes.get(),
                bodyStartElements = bodyStartElements,
                bodyAfterScriptElements = bodyAfterScriptElements,
                bodyEndElements = bodyEndElements,
                buildTarget = buildTarget
            )
        )

        // Log final body elements grouped by position
        if (bodyStartElements.isNotEmpty() || bodyAfterScriptElements.isNotEmpty() || bodyEndElements.isNotEmpty()) {
            logger.lifecycle("Final <body> elements:")
            if (bodyStartElements.isNotEmpty()) {
                logger.lifecycle("  START position:")
                bodyStartElements.forEach { logger.lifecycle("    ${it.replace("\n", "")}") }
            }
            if (bodyAfterScriptElements.isNotEmpty()) {
                logger.lifecycle("  AFTER_SCRIPT position (default):")
                bodyAfterScriptElements.forEach { logger.lifecycle("    ${it.replace("\n", "")}") }
            }
            if (bodyEndElements.isNotEmpty()) {
                logger.lifecycle("  END position:")
                bodyEndElements.forEach { logger.lifecycle("    ${it.replace("\n", "")}") }
            }
        }
    }
}
