package com.varabyte.kobweb.cli.common

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.KobwebProject
import com.varabyte.kotter.runtime.Session
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
data class GitVersion(val major: Int, val minor: Int, val patch: Int)

class GitClient {
    val version: GitVersion = runBlocking(Dispatchers.IO) {
        val process = Runtime.getRuntime().git("version")
        val versionDeferred = CompletableDeferred<GitVersion>()
        process.consumeProcessOutput { line, isError ->
            val result = GIT_VERSION_REGEX.matchEntire(line)
            if (result != null) {
                versionDeferred.complete(GitVersion(
                    result.groupValues[2].toInt(),
                    result.groupValues[3].toInt(),
                    result.groupValues[4].toInt(),
                ))
            } else {
                throw KobwebException("git must be installed and present on the path")
            }
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
