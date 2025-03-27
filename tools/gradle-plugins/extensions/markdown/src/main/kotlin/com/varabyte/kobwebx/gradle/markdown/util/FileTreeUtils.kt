package com.varabyte.kobwebx.gradle.markdown.util

import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitDetails

/**
 * Only visit files, not directories (which [FileTree.visit] includes.
 */
fun FileTree.visitFiles(block: FileVisitDetails.() -> Unit) {
    this.visit {
        if (isDirectory) return@visit
        block()
    }
}