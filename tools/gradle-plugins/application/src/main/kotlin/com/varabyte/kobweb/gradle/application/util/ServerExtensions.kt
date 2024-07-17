package com.varabyte.kobweb.gradle.application.util

import com.varabyte.kobweb.gradle.application.KOBWEB_SERVER_JAR
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.server.api.ServerState
import java.io.File

fun ServerState.toDisplayText(): String {
    return "http://localhost:$port (PID = $pid)"
}

internal fun KobwebFolder.getServerJar(): File {
    return this.path.resolve(KOBWEB_SERVER_JAR).toFile()
}
