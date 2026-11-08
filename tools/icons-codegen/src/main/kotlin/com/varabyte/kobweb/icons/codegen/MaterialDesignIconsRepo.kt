package com.varabyte.kobweb.icons.codegen

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
        val uri = gitHubRepo.rawFileUri(commit, path)
        val names = uri.openDownloadStream().use { it.readBytes().decodeToString() }
            .lineSequence()
            .filter { it.isNotBlank() }
            .map { it.substringBefore(' ') }
            .toList()
        check(names.isNotEmpty()) { "No icon codepoints parsed from $uri" }
        return names
    }

    companion object {
        private val gitHubRepo = GitHubRepo("google/material-design-icons")

        /** Resolves [spec] to a commit SHA. The repo has no release tags, so "latest" means the head of its main branch. */
        fun resolveCommit(spec: VersionSpec) = gitHubRepo.resolveCommit(spec, branch = "master")
    }
}
