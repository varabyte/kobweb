package com.varabyte.kobweb.icons.codegen

import java.net.URI

/**
 * Google's https://github.com/google/material-design-icons repo at some [commit], which hosts the fonts for both
 * Material Icons and Material Symbols.
 */
internal class MaterialDesignIconsRepo(private val commit: String) {
    /**
     * Downloads the icon font codepoints file at [path] (relative to the repo root) and returns its icon names, in file
     * order.
     *
     * Each line of a codepoints file maps a ligature name to its codepoint, e.g. "arrow_back e5c4".
     */
    fun fetchCodepointNames(path: String): List<String> {
        val uri = URI("https://raw.githubusercontent.com/google/material-design-icons/$commit/$path")
        val names = uri.openDownloadStream().use { it.readBytes().decodeToString() }
            .lineSequence()
            .filter { it.isNotBlank() }
            .map { it.substringBefore(' ') }
            .toList()
        check(names.isNotEmpty()) { "No icon codepoints parsed from $uri" }
        return names
    }
}
