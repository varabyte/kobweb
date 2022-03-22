package com.varabyte.kobweb.cli.common

import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotter.runtime.Session
import com.varabyte.kotter.runtime.concurrent.createKey
import java.nio.file.Files
import java.nio.file.Path

const val DEFAULT_REPO = "https://github.com/varabyte/kobweb-templates"
const val DEFAULT_BRANCH = "main"

private val TempDirKey = Session.Lifecycle.createKey<Path>()

/**
 * Fetch the target repository into a temporary location, returning it, or null if the fetch failed.
 *
 * The temp directory will be automatically deleted when the program exits.
 */
fun Session.handleFetch(gitClient: GitClient, repo: String, branch: String): Path? {
    val tempDir = Files.createTempDirectory("kobweb")
    data.set(TempDirKey, tempDir, dispose = { tempDir.toFile().deleteRecursively() })
    if (!processing("Cloning \"$repo\"") {
            gitClient.clone(repo, branch, tempDir)
        }) {
        section {
            textError("We were unable to fetch templates. Confirm the specified repository/branch and try again.")
        }.run()

        return null
    }
    section { textLine() }.run()

    return tempDir
}