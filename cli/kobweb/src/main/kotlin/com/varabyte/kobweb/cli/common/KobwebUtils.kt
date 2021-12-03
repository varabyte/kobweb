package com.varabyte.kobweb.cli.common

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.KobwebProject
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.konsole.runtime.KonsoleApp

fun assertKobwebProject(): KobwebProject {
    return try {
        KobwebProject()
    } catch (ex: KobwebException) {
        throw KobwebException("This command must be called in the root of a Kobweb project.")
    }
}

fun KonsoleApp.findKobwebProject(): KobwebProject? {
    return try {
        assertKobwebProject()
    } catch (ex: KobwebException) {
        informError(ex.message!!)
        null
    }
}

object KobwebGradle {
    fun startServer(env: ServerEnvironment, enableLiveReloading: Boolean = (env == ServerEnvironment.DEV)): Process {
        val args = mutableListOf("-PkobwebEnv=$env", "kobwebStart")
        if (enableLiveReloading) {
            args.add("-t") // Enable live reloading only while in dev mode
        }

        return Runtime.getRuntime().gradlew(*args.toTypedArray())
    }

    fun stopServer(): Process {
        return Runtime.getRuntime().gradlew("kobwebStop")
    }

    fun export(): Process {
        return Runtime.getRuntime()
            .gradlew("-PkobwebReuseServer=false", "-PkobwebBuildTarget=RELEASE", "kobwebExport")
    }
}