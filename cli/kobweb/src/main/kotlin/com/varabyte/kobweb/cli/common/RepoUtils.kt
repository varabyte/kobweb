package com.varabyte.kobweb.cli.common

import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotter.runtime.Session
import com.varabyte.kotter.runtime.concurrent.createKey
import org.eclipse.jgit.api.Git
import java.io.File
import java.nio.file.Files

const val DEFAULT_REPO = "https://github.com/varabyte/kobweb-templates"
const val DEFAULT_BRANCH = "main"

private val TempDirKey = Session.Lifecycle.createKey<File>()

/**
 * Fetch the target repository into a temporary location, returning it, or null if the fetch failed.
 *
 * The temp directory will be automatically deleted when the program exits.
 */
fun Session.handleFetch(repo: String, branch: String): File? {
    val tempDir = Files.createTempDirectory("kobweb").toFile()
    data.set(TempDirKey, tempDir, dispose = { tempDir.deleteRecursively() })
    if (!processing("Cloning \"$repo\"") {
            Git.cloneRepository()
                .setURI(repo)
                .setBranch(branch)
                .setDirectory(tempDir)
                .call()
        }) {
        section {
            textError("We were unable to fetch templates. Confirm the specified repository/branch and try again.")
        }.run()

        return null
    }
    section { textLine() }.run()

    return tempDir
}