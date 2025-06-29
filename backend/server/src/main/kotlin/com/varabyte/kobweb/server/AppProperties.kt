package com.varabyte.kobweb.server

import io.ktor.server.application.*
import java.util.*

class AppProperties(
    val kobwebVersion: String,
    val ktorVersion: String,
) {
    companion object {
        fun fromManifest(): AppProperties {
            val appProperties = Properties()
            val manifest = Application::class.java.classLoader.getResource("META-INF/MANIFEST.MF")!!
            manifest.openStream().use { inputString ->
                appProperties.load(inputString)
            }

            return AppProperties(
                appProperties.getProperty("Kobweb-Version")!!,
                appProperties.getProperty("Ktor-Version")!!,
            )
        }
    }
}