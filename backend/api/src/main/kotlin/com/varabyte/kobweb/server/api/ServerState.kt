package com.varabyte.kobweb.server.api

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.KobwebFolder
import com.varabyte.kobweb.common.io.KobwebReadableFile
import kotlinx.serialization.Serializable

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

class ServerStateFile(kobwebFolder: KobwebFolder) : KobwebReadableFile<ServerRequests>(
    kobwebFolder,
    "server/state.yaml",
    deserialize = { text -> Yaml.default.decodeFromString(ServerRequests.serializer(), text) }
)