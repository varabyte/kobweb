package com.varabyte.kobweb.server.api

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.KobwebFolder
import kotlinx.serialization.Serializable
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.getLastModifiedTime

enum class ServerStatus {
    STOPPED,
    RUNNING,
}

@Serializable
data class ServerState(
    val status: ServerStatus,
    val port: Int,
    val pid: Int,
)

// TODO(Bug #12): Use safer file logic here to protect against multiple writers etc.
class ServerStateFile(kobwebFolder: KobwebFolder) {
    private val filePath = kobwebFolder.resolve("server/state.yaml")

    var lastModified = 0L
    lateinit var _serverState: ServerState
    val serverState: ServerState?
        get() {
            return filePath
                .takeIf { it.exists() }
                ?.let {
                    val lastModified = filePath.getLastModifiedTime()
                    if (this.lastModified != lastModified.toMillis()) {
                        this.lastModified = lastModified.toMillis()
                        _serverState = Yaml.default.decodeFromString(ServerState.serializer(), filePath.toFile().readText())
                    }

                    _serverState
                }
        }

    fun updateState(state: ServerState) {
        if (!filePath.parent.exists()) {
            filePath.parent.createDirectories()
        }
        filePath.toFile().writeText(Yaml.default.encodeToString(ServerState.serializer(), state))
    }
}