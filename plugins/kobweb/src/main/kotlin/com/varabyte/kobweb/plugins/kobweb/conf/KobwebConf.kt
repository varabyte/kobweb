package com.varabyte.kobweb.plugins.kobweb.conf

import kotlinx.serialization.Serializable

@Serializable
class Site(val title: String)

@Serializable
class Server(val port: Int = 8081)

@Serializable
class KobwebConf(
    val site: Site,
    val server: Server = Server(),
)