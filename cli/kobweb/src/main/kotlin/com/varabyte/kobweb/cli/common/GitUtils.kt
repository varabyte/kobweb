package com.varabyte.kobweb.cli.common

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kotter.runtime.Session
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.absolutePathString

fun Session.findGit(): GitClient? {
    return try {
        return GitClient()
    } catch (ex: KobwebException) {
        informError(ex.message!!)
        null
    }
}

private val GIT_VERSION_REGEX = Regex("""git.* ((\d+).(\d+).(\d+))""")

sealed interface GitVersion {
    data class Parsed(val major: Int, val minor: Int, val patch: Int) : GitVersion
    // If we detect git (e.g. we're able to run the process), we should always move forward
    // if we can, even if we can't parse the version.
    data class Unparsed(val text: String) : GitVersion
}

class GitClient {
    val version: GitVersion = runBlocking(Dispatchers.IO) {
        val process = try {
            Runtime.getRuntime().git("version")
        } catch (ex: IOException) {
            throw KobwebException("git must be installed and present on the path")
        }
        val versionDeferred = CompletableDeferred<GitVersion>()
        process.consumeProcessOutput { line, _ ->
            val result = GIT_VERSION_REGEX.find(line)
            versionDeferred.complete(
                if (result != null) {
                    GitVersion.Parsed(
                        result.groupValues[2].toInt(),
                        result.groupValues[3].toInt(),
                        result.groupValues[4].toInt(),
                    )
                } else {
                    GitVersion.Unparsed(line)
                }
            )
        }
        versionDeferred.await()
    }

    private fun git(vararg args: String) {
        Runtime.getRuntime().git(*args).waitFor()
    }

    fun clone(
        repo: String,
        branch: String,
        into: Path,
        shallow: Boolean = true,
    ) {
        val args = mutableListOf("clone")
        if (shallow) {
            args.add("--depth")
            args.add("1")
        }
        args.add(repo)
        args.add("-b")
        args.add(branch)
        args.add(into.absolutePathString())

        git(*args.toTypedArray())
    }

    fun init(rootDir: Path? = null) {
        val args = mutableListOf("init")
        if (rootDir != null) {
            args.add(rootDir.absolutePathString())
        }

        git(*args.toTypedArray())
    }

    fun add(filePattern: String, rootDir: Path? = null) {
        val args = mutableListOf("add", filePattern)
        if (rootDir != null) {
            args.addAll(0, listOf("-C", rootDir.absolutePathString()))
        }
        git(*args.toTypedArray())
    }

    fun commit(message: String, rootDir: Path? = null) {
        val args = mutableListOf("commit", "-m", message)
        if (rootDir != null) {
            args.addAll(0, listOf("-C", rootDir.absolutePathString()))
        }
        git(*args.toTypedArray())
    }
}
