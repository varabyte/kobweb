package com.varabyte.kobweb.server.plugins

import com.varabyte.kobweb.project.conf.KobwebConf
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*

fun Application.configureHTTP(conf: KobwebConf) {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")

        // Ktor wants to report the "Server" header as "Ktor/$version" (e.g. "Ktor/2.3.0"), but it uses a manifest
        // setting to get that version which is lost when we build the server as a fat jar. Instead, we create our own
        // manifest setting and use that here, side-stepping the issue. If we don't do this, the ktor server runs fine
        // but reports its version as "ktor/debug" which makes it look like Kobweb goofed up.
        // See also: build.gradle.kts where it sets the version
        // See also: the implementation for io.ktor.server.plugins.defaultheaders.DefaultHeaders that we're essentially
        //   overriding here.
        Application::class.java.classLoader.getResource("META-INF/MANIFEST.MF")?.let { manifest ->
            manifest.openStream().use { inputString ->
                val properties = java.util.Properties()
                properties.load(inputString)
                val version: String? = properties.getProperty("Ktor-Version")
                if (version != null) {
                    header(HttpHeaders.Server, "Ktor/$version")
                }
            }
        }
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowCredentials = true
        allowNonSimpleContentTypes = true // Kobweb uses octet-streams

        conf.server.cors.hosts.forEach { host -> allowHost(host.name, host.schemes, host.subdomains) }
    }

    install(Compression) {
        excludeContentType(
            ContentType.Video.Any,
            ContentType.Image.JPEG,
            ContentType.Image.PNG,
            ContentType.Audio.Any,
            ContentType.MultiPart.Any,
            ContentType.Text.EventStream
        )
        minimumSize(1024)

        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
        }
    }

    install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                else -> null
            }
        }
    }
}
