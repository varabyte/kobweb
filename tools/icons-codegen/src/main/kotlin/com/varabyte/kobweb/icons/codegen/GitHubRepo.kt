package com.varabyte.kobweb.icons.codegen

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URI

/** A public GitHub repository, e.g. "lucide-icons/lucide", queried through GitHub's REST API. */
internal class GitHubRepo(private val name: String) {
    /** Returns the names of all of this repo's tags, e.g. "1.70.0". */
    fun fetchTagNames(): List<String> {
        val refs = URI("https://api.github.com/repos/$name/git/matching-refs/tags/")
            .openDownloadStream()
            .use { Json.parseToJsonElement(it.readBytes().decodeToString()).jsonArray }
        return refs.map { it.jsonObject.getValue("ref").jsonPrimitive.content.removePrefix("refs/tags/") }
    }

    /** Returns the SHA of the latest commit on [branch]. */
    fun fetchHeadCommit(branch: String): String {
        return URI("https://api.github.com/repos/$name/commits/$branch")
            .openDownloadStream()
            .use { Json.parseToJsonElement(it.readBytes().decodeToString()).jsonObject }
            .getValue("sha").jsonPrimitive.content
    }

    /** Returns the URI that serves the raw contents of the file at [path] (relative to the repo root) at [ref]. */
    fun rawFileUri(ref: String, path: String) = URI("https://raw.githubusercontent.com/$name/$ref/$path")

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
}
