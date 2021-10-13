package com.varabyte.kobweb.server.api

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.io.KobwebReadableTextFile
import kotlinx.serialization.Serializable

@Serializable
data class ServerState(
    val env: ServerEnvironment,
    val port: Int,
    val pid: Long,
) {
    fun isRunning() = ProcessHandle.of(pid).isPresent
}

class ServerStateFile(kobwebFolder: KobwebFolder) : KobwebReadableTextFile<ServerState>(
    kobwebFolder,
    "server/state.yaml",
    deserialize = { text -> Yaml.default.decodeFromString(ServerState.serializer(), text) }
)