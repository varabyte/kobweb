package com.varabyte.kobweb.gradle.core.icons

import java.io.File

/**
 * What `kobwebFetchIcons` recorded about an icon set's fetched data, so builds can tell when that data is out of date.
 *
 * It's checked in next to the data, as a `<id>.lock` file.
 */
internal class IconsLock(val version: String) {
    fun writeTo(file: File) {
        file.writeText(
            """
            |# Written by the kobwebFetchIcons task. Check this file in, but don't edit it by hand.
            |version=$version
            |
            """.trimMargin()
        )
    }

    companion object {
        /** The lock file for the icon set with this [id], inside a module's icons store [storeDir]. */
        fun fileFor(storeDir: File, id: String): File = storeDir.resolve("$id.lock")

        /** Reads a lock file written by [writeTo], returning null if it doesn't exist. */
        fun readFrom(file: File): IconsLock? {
            if (!file.exists()) return null
            val entries = file.readLines()
                .asSequence()
                .filter { line -> line.isNotBlank() && !line.startsWith("#") }
                .mapNotNull { line -> line.split('=', limit = 2).takeIf { it.size == 2 } }
                .associate { (key, value) -> key to value }
            return entries["version"]?.let(::IconsLock)
        }
    }
}
