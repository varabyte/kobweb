package com.varabyte.kobweb.cli.common

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.KobwebProject
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kotter.runtime.Session

fun assertKobwebProject(): KobwebProject {
    return try {
        KobwebProject()
    } catch (ex: KobwebException) {
        throw KobwebException("This command must be called in the root of a Kobweb project.")
    }
}

fun Session.findKobwebProject(): KobwebProject? {
    return try {
        assertKobwebProject()
    } catch (ex: KobwebException) {
        informError(ex.message!!)
        null
    }
}

class KobwebGradle(private val env: ServerEnvironment) {
    private fun gradlew(vararg args: String): Process {
        val finalArgs = args.toMutableList()
        if (env == ServerEnvironment.PROD) {
            // When in production, we don't want to leave a daemon running around hoarding resources unecessarily
            finalArgs.add("--no-daemon")
        }

        return Runtime.getRuntime().gradlew(*finalArgs.toTypedArray())
    }

    fun startServer(enableLiveReloading: Boolean = (env == ServerEnvironment.DEV)): Process {
        val args = mutableListOf("-PkobwebEnv=$env", "kobwebStart")
        if (enableLiveReloading) {
            args.add("-t")
        }
        return gradlew(*args.toTypedArray())
    }

    fun stopServer(): Process {
        return gradlew("kobwebStop")
    }

    fun export(): Process {
        return gradlew("-PkobwebReuseServer=false", "-PkobwebBuildTarget=RELEASE", "kobwebExport")
    }
}