package com.varabyte.kobweb.gradle.core.util

import java.util.*

// version.properties is automatically generated in build.gradle.kts with the current version
internal object VersionUtil {
    val version: String by lazy {
        Properties().apply { load(VersionUtil::class.java.getResourceAsStream("/version.properties")) }
            .getProperty("version") ?: error("version property not found")
    }
}
