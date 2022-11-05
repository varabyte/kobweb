package com.varabyte.kobweb.gradle.core.utils

import java.io.File
import java.util.zip.ZipInputStream

fun File.isDescendantOf(maybeAncestorTest: (File) -> Boolean): Boolean {
    var curr: File? = this
    while (curr != null) {
        if (maybeAncestorTest(curr)) {
            return true
        }
        curr = curr.parentFile
    }
    return false
}

fun File.isDescendantOf(maybeAncestor: File): Boolean = isDescendantOf { ancestor -> ancestor == maybeAncestor }

/**
 * If the target file is a zip file, search it for a single file that it may contain.
 *
 * @param path The path to the file, e.g. `markdown/blog/Post.md`
 * @param handle A handler which will be fed with the unzipped bytes if found.
 */
fun File.searchZipFor(path: String, handle: (ByteArray) -> Unit) {
    ZipInputStream(inputStream()).use { zis ->
        var entry = zis.nextEntry
        while (entry != null) {
            if (entry.name == path) {
                handle(zis.readAllBytes())
                break
            }
            entry = zis.nextEntry
        }
    }
}
