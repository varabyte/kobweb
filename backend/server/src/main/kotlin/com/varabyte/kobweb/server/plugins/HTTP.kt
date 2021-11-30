package com.varabyte.kobweb.server.plugins

import com.varabyte.kobweb.project.conf.KobwebConf
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*

fun Application.configureHTTP(conf: KobwebConf) {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true

        conf.server.cors.hosts.forEach { host -> host(host.name, host.schemes, host.subDomains) }
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
        options { outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                else -> null
            }
        }
    }
}