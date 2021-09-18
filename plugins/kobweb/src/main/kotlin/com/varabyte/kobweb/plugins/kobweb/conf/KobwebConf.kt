package com.varabyte.kobweb.plugins.kobweb.conf

import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
class Site(val title: String)

@Serializable
class Locations(
    /**
     * The root package of all pages.
     *
     * Any composable function not under this root will be ignored, even if annotated by @Page.
     *
     * An initial '.' means this should be prefixed by the project group, e.g. ".pages" -> "com.example.pages"
     */
    val pages: String = ".pages",

    /**
     * The path of public resources inside the project's resources folder, e.g. "public" ->
     * "src/jsMain/resources/public"
     */
    val public: String = "public",
) {
    fun getPagesPackage(projectGroup: String): String {
        return when {
            pages.startsWith('.') -> "$projectGroup$pages"
            else -> pages
        }
    }

    fun getPublicPath(resourcesPath: Path) = resourcesPath.resolve(public)
}

@Serializable
class Server(val port: Int = 8080)

@Serializable
class KobwebConf(
    val site: Site,
    val locations: Locations = Locations(),
    val server: Server = Server(),
)