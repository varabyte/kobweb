package com.varabyte.kobweb.icons.codegen.fetch

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.InputStream
import java.net.URI

/** A public GitHub repository, e.g. "lucide-icons/lucide", which upstream icon data is downloaded from. */
internal class GitHubRepo(private val name: String) {
    /** Returns the names of all of this repo's tags, e.g. "1.70.0". */
    fun fetchTagNames(): List<String> {
        return fetchApiJson("git/matching-refs/tags/").jsonArray
            .map { it.jsonObject.getValue("ref").jsonPrimitive.content.removePrefix("refs/tags/") }
    }

    /** Returns the SHA of the latest commit on [branch]. */
    fun fetchHeadCommit(branch: String): String {
        return fetchApiJson("commits/$branch").jsonObject.getValue("sha").jsonPrimitive.content
    }

    /** Downloads the text of the file at [path] (relative to the repo root) at [ref], e.g. a tag or commit SHA. */
    fun fetchFileText(ref: String, path: String): String {
        return fileUri(ref, path).openDownloadStream().readAllText()
    }

    /**
     * The URL for downloading the file at [path] at [ref].
     *
     * [path] is the file's real name in the repo, which gets percent-encoded here, e.g. the brackets in
     * "MaterialSymbolsOutlined[FILL,GRAD,opsz,wght].codepoints".
     */
    internal fun fileUri(ref: String, path: String): URI {
        // This URI constructor percent-encodes characters that aren't legal in a path, unlike URI(String)
        return URI("https", "raw.githubusercontent.com", "/$name/$ref/$path", null)
    }

    /** Opens a stream for downloading a zip archive of the whole repo at [tag]. */
    fun openTagArchiveStream(tag: String): InputStream {
        return URI("https://github.com/$name/archive/refs/tags/$tag.zip").openDownloadStream()
    }

    /** Resolves [spec] to one of this repo's release tags, only looking them up if the spec isn't exact. */
    fun resolveRelease(spec: VersionSpec): String {
        if (spec.isExact) return spec.text
        return spec.pickFrom(fetchTagNames()) ?: error("No release of $name matches version \"${spec.text}\".")
    }

    /**
     * Resolves [spec] to a commit SHA, for repos without release tags. Only an exact commit or [VersionSpec.LATEST]
     * (the head of [branch]) works.
     */
    fun resolveCommit(spec: VersionSpec, branch: String): String {
        return when {
            spec.isLatest -> fetchHeadCommit(branch)
            spec.isExact -> spec.text
            else -> throw IllegalArgumentException(
                "Versions of $name are commit SHAs, so \"${spec.text}\" can't be resolved. " +
                    "Use a commit SHA or \"${VersionSpec.LATEST}\" instead."
            )
        }
    }

    /** Queries GitHub's REST API at [path], relative to this repo's API root. */
    private fun fetchApiJson(path: String): JsonElement {
        return Json.parseToJsonElement(URI("https://api.github.com/repos/$name/$path").openDownloadStream().readAllText())
    }

    private fun InputStream.readAllText() = use { it.readBytes().decodeToString() }

    /** Opens a connection for downloading from this URI, logging what's being fetched. */
    private fun URI.openDownloadStream(): InputStream {
        println("Fetching $this")
        val connection = toURL().openConnection()
        connection.setRequestProperty("User-Agent", "kobweb-icons-codegen")
        return connection.getInputStream()
    }
}
