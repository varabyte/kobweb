package com.varabyte.kobweb.cli.common

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.KobwebProject
import com.varabyte.kobweb.server.api.SiteLayout
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
        finalArgs.add("--stacktrace")
        if (env == ServerEnvironment.PROD) {
            // When in production, we don't want to leave a daemon running around hoarding resources unecessarily
            finalArgs.add("--no-daemon")
        }

        return Runtime.getRuntime().gradlew(*finalArgs.toTypedArray())
    }

    fun startServer(enableLiveReloading: Boolean, siteLayout: SiteLayout): Process {
        val args = mutableListOf("-PkobwebEnv=$env", "-PkobwebRunLayout=$siteLayout", "kobwebStart")
        if (enableLiveReloading) {
            args.add("-t")
        }
        return gradlew(*args.toTypedArray())
    }

    fun stopServer(): Process {
        return gradlew("kobwebStop")
    }

    fun export(siteLayout: SiteLayout): Process {
        // Even if we are exporting a non-Kobweb layout, we still want to start up a dev server using a Kobweb layout so
        // it looks for the source files in the right place.
        return gradlew("-PkobwebReuseServer=false", "-PkobwebEnv=DEV", "-PkobwebRunLayout=KOBWEB", "-PkobwebBuildTarget=RELEASE", "-PkobwebExportLayout=$siteLayout", "kobwebExport")
    }
}