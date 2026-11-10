package com.varabyte.kobweb.icons.codegen.fetch

import java.io.InputStream
import java.net.URI

/** A public GitHub repository, e.g. "lucide-icons/lucide", which upstream icon data is downloaded from. */
internal class GitHubRepo(private val name: String) {
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

    private fun InputStream.readAllText() = use { it.readBytes().decodeToString() }

    /** Opens a connection for downloading from this URI, logging what's being fetched. */
    private fun URI.openDownloadStream(): InputStream {
        println("Fetching $this")
        val connection = toURL().openConnection()
        connection.setRequestProperty("User-Agent", "kobweb-icons-codegen")
        return connection.getInputStream()
    }
}
