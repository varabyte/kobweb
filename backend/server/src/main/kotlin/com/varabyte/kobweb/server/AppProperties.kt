package com.varabyte.kobweb.server

import io.ktor.server.application.*
import java.io.File
import java.util.jar.JarFile

class AppProperties(
    val kobwebVersion: String,
    val ktorVersion: String,
) {
    companion object {
        fun fromManifest(): AppProperties {
            // Get this JAR's manifest to read app properties we added from Gradle.
            // Using `protectionDomain.codeSource` directly targets our JAR, solving a classpath shadowing
            // issue where searching for "META-INF/MANIFEST.MF" could return the wrong file from a dependency.
            val uri = Application::class.java.protectionDomain.codeSource.location.toURI()
            val attributes = JarFile(File(uri)).use { it.manifest }.mainAttributes
            return AppProperties(
                attributes.getValue("Kobweb-Version")!!,
                attributes.getValue("Ktor-Version")!!,
            )
        }
    }
}