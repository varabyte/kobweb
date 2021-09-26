package com.varabyte.kobweb.common.conf

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.KobwebFolder
import com.varabyte.kobweb.common.io.KobwebReadableFile
import kotlinx.serialization.Serializable

@Serializable
class Site(val title: String)

@Serializable
class Server(val port: Int = 8080)

@Serializable
class KobwebConf(
    val site: Site,
    val server: Server = Server(),
)

class KobwebConfFile(kobwebFolder: KobwebFolder) : KobwebReadableFile<KobwebConf>(
    kobwebFolder,
    "conf.yaml",
    deserialize = { text -> Yaml.default.decodeFromString(KobwebConf.serializer(), text) }
)