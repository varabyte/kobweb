package com.varabyte.kobweb.server.io

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.yaml.nonStrictDefault
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.io.KobwebWritableTextFile
import com.varabyte.kobweb.server.api.ServerState

// Note: Unlike the version of this class in the API module, this one is writable.
class ServerStateFile(kobwebFolder: KobwebFolder) : KobwebWritableTextFile<ServerState>(
    kobwebFolder,
    "server/state.yaml",
    serialize = { requests -> Yaml.nonStrictDefault.encodeToString(ServerState.serializer(), requests) },
    deserialize = { text -> Yaml.nonStrictDefault.decodeFromString(ServerState.serializer(), text) }
)