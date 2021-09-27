package com.varabyte.kobweb.server.api

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.KobwebFolder
import com.varabyte.kobweb.common.io.KobwebReadableFile
import kotlinx.serialization.Serializable

@Serializable
data class ServerState(
    val port: Int,
    val pid: Long,
)

class ServerStateFile(kobwebFolder: KobwebFolder) : KobwebReadableFile<ServerState>(
    kobwebFolder,
    "server/state.yaml",
    deserialize = { text -> Yaml.default.decodeFromString(ServerState.serializer(), text) }
)