@file:Suppress("unused") // Used by serializer

package com.varabyte.kobweb.common.conf

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.KobwebFolder
import com.varabyte.kobweb.common.io.KobwebReadableFile
import kotlinx.serialization.Serializable

@Serializable
class Site(val title: String)

@Serializable
class Server(
    val files: Files,
    val port: Int = 8080
) {
    @Serializable
    class Files(
        val dev: Dev,
        val prod: Prod = Prod(),
    ) {
        /**
         * The dev server only serves a single html file that represents the whole project.
         */
        @Serializable
        class Dev(
            /** The path to serve content from, which includes the Kobweb index.html file. */
            val contentRoot: String,
            /** The path to the final JavaScript file generated from the user's Kotlin code. */
            val script: String,
        )

        /**
         * The prod server serves static files but needs a fallback in case one is missing.
         */
        @Serializable
        class Prod(
            /** The path to the root of where the static site lives */
            val siteRoot: String = KobwebFolder.pathNameTo("site"),
            /** The path to serve content from. If not set, defaults to the value set in [Dev.contentRoot] */
            val contentRoot: String? = null,
        )
    }
}

@Serializable
class KobwebConf(
    val site: Site,
    val server: Server,
)

class KobwebConfFile(kobwebFolder: KobwebFolder) : KobwebReadableFile<KobwebConf>(
    kobwebFolder,
    "conf.yaml",
    deserialize = { text -> Yaml.default.decodeFromString(KobwebConf.serializer(), text) }
)