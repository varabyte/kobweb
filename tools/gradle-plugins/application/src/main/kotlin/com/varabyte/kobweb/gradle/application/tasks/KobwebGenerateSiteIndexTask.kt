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
import com.varabyte.kobweb.gradle.core.util.toSha256
import com.varabyte.kobweb.icons.codegen.IconSet
import com.varabyte.kobweb.icons.codegen.stylesheet.IconStylesheet
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_LIBRARY
import com.varabyte.kobweb.project.common.nonStrictDefault
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
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.util.prefixIfNot
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import java.net.URI
import javax.inject.Inject

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

@CacheableTask
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

    // Intentionally don't use @CompileClasspath, we just want to know if things have changed at all as a signal to
    // search all jars.
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val compileClasspath: ConfigurableFileCollection

    @get:Internal
    abstract val dependencies: ListProperty<ResolvedDependencyResult>

    /**
     * Icon sets the app generates bindings for in `kobweb { icons { ... } }`, mapped to their locked versions, e.g.
     * `"fa" to "6.5.2"`.
     */
    @get:Input
    abstract val iconSetVersions: MapProperty<String, String>

    /**
     * Icon sets the app generates bindings for, mapped to whether to warn when their stylesheet may not render every
     * library's icons.
     */
    @get:Input
    abstract val warnOnVersionMismatch: MapProperty<String, Boolean>

    @get:Input
    val hasFaDependency: Provider<Boolean>
        get() = dependencies.hasDependencyNamed("com.varabyte.kobwebx:silk-icons-fa")

    @get:Input
    val hasMdiDependency: Provider<Boolean>
        get() = dependencies.hasDependencyNamed("com.varabyte.kobwebx:silk-icons-mdi")

    @get:Input
    val hasMsDependency: Provider<Boolean>
        get() = dependencies.hasDependencyNamed("com.varabyte.kobwebx:silk-icons-ms")

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

        val urlStr = url
        @Suppress("NAME_SHADOWING") // String to actual URL
        val url = URI(url).toURL()
        val downloadResult = projectLayout.downloadOrCached(logger, url)
        val selfHostPrefix = "_kobweb/self-host"
        val selfHostRoot = getGenPublicRoot().resolve(selfHostPrefix)

        var urlPath = url.path.removePrefix("/")

        // If the download URL has parameters, instead of dropping them, include them in the final URL. This can help us
        // avoid conflicts if multiple modules end up otherwise resolving to the same base URL (which happens with
        // Google MDI icons and Google MS icons, for example; same base path, different parameters).
        // e.g. "a/b/c/slug?hello=world" -> "a/b/c/SOMEHASHHERE/slug"
        if (url.query != null) {
            val urlWithoutSlug = urlPath.substringBeforeLast("/", "")
            val slug = urlPath.substringAfterLast("/")

            urlPath = listOf(
                urlWithoutSlug,
                // Hash the params, as a lazy way to avoid characters that don't play nice with the filesystem.
                url.query.toSha256(),
                slug
            ).filter { it.isNotEmpty() }.joinToString("/")
        }

        // Slight hack: detecting CSS files is really important for self-hosting (since we search css files for internal
        // links that we also need to self-host), but in at least one case we've seen a URL that is a CSS file but had
        // no extension. Normally, when you download a file, its content type is included in the response, but we lose
        // that information when saving the file to disk. Appending an explicit css extension works around that.
        if (
            !urlPath.substringAfterLast("/").contains(".")
            && downloadResult.metadata.contentType?.contains("text/css") == true
        ) {
            urlPath += ".css"
        }

        val selfHostedFile = selfHostRoot.resolveWithMkdirs(urlPath).apply { writeBytes(downloadResult.file.readBytes()) }

        if (urlPath.endsWith(".css")) {
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
                                val urlBase = urlStr.substringBeforeLast("/")
                                // urlValue.removePrefix("./") removes useless "." in the middle of a URL that seems to
                                // confuse the downloader.
                                // Good: `/url/base` + './nested/value` -> "/url/base/nested/value`
                                // Bad:  `/url/base` + './nested/value` -> "/url/base/./nested/value`
                                urlBase + "/" + urlValue.removePrefix("./") + url.query?.let { "?$it" }.orEmpty() + url.ref?.let { "#$it" }.orEmpty()
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

        return basePath.prependTo("/$selfHostPrefix/$urlPath")
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

    /** The name of the dependency this artifact belongs to, e.g. "sitelib-js-1.0.klib" -> "sitelib". */
    private val File.dependencyName get() = nameWithoutExtension.substringBeforeLast("-js-")

    /**
     * Returns a stylesheet link for each icon set that this app or its dependencies generate bindings for.
     *
     * A page can only load one stylesheet per set, since different versions share class names and the later one wins.
     * The app's own version is its explicit choice, so it wins; otherwise, the icon set picks the version to load.
     */
    private fun iconStylesheetLinks(librariesMetadata: Map<File, LibraryMetadata>): List<String> {
        val appIconSetVersions = iconSetVersions.get()

        // Icon set id -> the modules that generate its bindings
        val moduleVersionsByIconSet = mutableMapOf<String, ModuleIconVersions>()
        fun addBindings(id: String, module: String, version: String) {
            moduleVersionsByIconSet.getOrPut(id) { ModuleIconVersions() }[module] = version
        }
        appIconSetVersions.forEach { (id, version) -> addBindings(id, module = "this app", version) }
        if (hasFaDependency.get()) addBindings("fa", module = "silk-icons-fa", SILK_ICONS_VERSIONS.getValue("fa"))
        if (hasMdiDependency.get()) addBindings("mdi", module = "silk-icons-mdi", SILK_ICONS_VERSIONS.getValue("mdi"))
        if (hasMsDependency.get()) addBindings("ms", module = "silk-icons-ms", SILK_ICONS_VERSIONS.getValue("ms"))
        librariesMetadata.forEach { (file, libraryMetadata) ->
            libraryMetadata.iconSets.forEach { (id, version) -> addBindings(id, module = file.dependencyName, version) }
        }

        return IconSet.all.mapNotNull { iconSet ->
            val moduleVersions = moduleVersionsByIconSet[iconSet.id] ?: return@mapNotNull null
            val stylesheet = iconSet.stylesheet ?: return@mapNotNull null
            val version = appIconSetVersions[iconSet.id] ?: stylesheet.versionFor(moduleVersions.versions)
            val incompatibleModules = moduleVersions.incompatibleWith(stylesheet, version)
            if (incompatibleModules.isNotEmpty() && warnOnVersionMismatch.get()[iconSet.id] != false) {
                val mappings = incompatibleModules.entries.joinToString { (module, bindingsVersion) ->
                    "\"$module\" -> \"$bindingsVersion\""
                }
                val id = iconSet.id
                logger.warn(
                    "w: Only one \"$id\" icon stylesheet can be loaded, so this site loads it for version $version. " +
                        "But given conflicting module mappings ($mappings), if any of those icons were renamed or " +
                        "removed in $version, they will render blank. To silence this warning, you can update your " +
                        "dependencies (if possible), set this app's \"$id\" version explicitly (via " +
                        "`kobweb { icons { $id { version.set(\"...\") } } }`), or disable this warning (via " +
                        "`kobweb { icons { $id { warnOnVersionMismatch.set(false) } } }`)."
                )
            }

            HtmlUtil.serializeHeadContents {
                link {
                    rel = "stylesheet"
                    href = stylesheet.urlFor(version)
                }
            }
        }
    }

    @TaskAction
    fun execute() {
        // Collect all <head> elements together. These will almost always be defined only by the app, but libraries are
        // allowed to declare some as well. If they do, they will have embedded serialized html in their library
        // artifacts.
        val headElements = mutableListOf(indexBlock.serializedHead.get())

        // Read every Kobweb library's metadata up front, since both icon stylesheets and library <head> elements need it
        val librariesMetadata: Map<File, LibraryMetadata> = compileClasspath.mapNotNull { file ->
            var libraryMetadata: LibraryMetadata? = null
            file.searchZipFor(KOBWEB_METADATA_LIBRARY) { bytes ->
                libraryMetadata = Json.nonStrictDefault.decodeFromString(LibraryMetadata.serializer(), bytes.decodeToString())
            }
            libraryMetadata?.let { file to it }
        }.toMap()

        headElements.addAll(iconStylesheetLinks(librariesMetadata))

        val headElementsFromDependencies = mutableMapOf<File, List<String>>()
        librariesMetadata.forEach { (file, libraryMetadata) ->
            libraryMetadata.index.headElements?.let { headElementsStr ->
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

        val basePath = BasePath(confInputs.basePath)
        getGenIndexFile().get().asFile.writeText(
            createIndexFile(
                confInputs.title,
                indexBlock.lang.get(),
                headElements.applyUrlInterceptors(basePath).also { result ->
                    logger.lifecycle("Final <head> elements:")
                    logger.lifecycle("```")
                    result.elements.forEach { logger.lifecycle("  ${it.replace("\n", "")}") }
                    logger.lifecycle("```")

                    headElementsFromDependencies.forEach { (artifact, elements) ->
                        val dep = artifact.dependencyName
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
                basePath.prependTo(confInputs.script.substringAfterLast("/").prefixIfNot("/")),
                indexBlock.scriptAttributes.get(),
                buildTarget
            )
        )
    }

    companion object {
        /** The icon set versions that Kobweb's published silk-icons-* artifacts generate bindings for. */
        // TODO: Delete this once icon artifacts publish which icon set versions they use. Until then, keep
        //  it in sync with the versions in frontend/silk-icons-*/build.gradle.kts.
        private val SILK_ICONS_VERSIONS = mapOf(
            "fa" to "6.5.2",
            "mdi" to "49d4db35df873165d6bd6ba09b063c7dafbac2f4",
            "ms" to "49d4db35df873165d6bd6ba09b063c7dafbac2f4",
        )
    }
}

/** The modules that generate bindings for one icon set, each with the version it generated them at. */
private class ModuleIconVersions {
    private val versionsByModule = mutableMapOf<String, String>()

    val versions: Collection<String> get() = versionsByModule.values

    operator fun set(module: String, version: String) {
        versionsByModule[module] = version
    }

    /** The modules (with their versions) whose icons [stylesheet] at [stylesheetVersion] might not render. */
    fun incompatibleWith(stylesheet: IconStylesheet, stylesheetVersion: String): Map<String, String> {
        return versionsByModule.filterValues { version -> !stylesheet.serves(stylesheetVersion, version) }
    }
}
