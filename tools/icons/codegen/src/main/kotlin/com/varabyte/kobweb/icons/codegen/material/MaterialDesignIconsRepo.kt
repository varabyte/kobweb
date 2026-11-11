package com.varabyte.kobweb.icons.codegen.material

import com.varabyte.kobweb.icons.codegen.fetch.GitHubRepo
import com.varabyte.kobweb.icons.model.material.MdiIconSet
import com.varabyte.kobweb.icons.parsers.material.GoogleCodepoints

/**
 * Google's https://github.com/google/material-design-icons repo at some [commit], which hosts the fonts for both
 * Material Icons and Material Symbols.
 */
internal class MaterialDesignIconsRepo(private val commit: String) {
    /**
     * Downloads the icon font codepoints file at [path] (relative to the repo root) and returns its icon names, in file
     * order.
     */
    fun fetchCodepointNames(path: String): List<String> {
        val names = GoogleCodepoints.parse(gitHubRepo.fetchFileText(commit, path)).codepoints.keys.toList()
        check(names.isNotEmpty()) { "No icon codepoints parsed from $path at $commit" }
        return names
    }

    companion object {
        private val gitHubRepo = GitHubRepo(MdiIconSet.gitHubRepo)
    }
}
