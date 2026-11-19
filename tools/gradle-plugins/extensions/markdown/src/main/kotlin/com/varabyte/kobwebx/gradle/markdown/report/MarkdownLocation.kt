package com.varabyte.kobwebx.gradle.markdown.report

import java.io.File
import java.nio.file.Path
import kotlin.io.path.invariantSeparatorsPathString

/**
 * A place in a Markdown file that a report is about.
 *
 * @property file The Markdown file.
 * @property relativePath The path of [file] relative to its Markdown root, e.g. `blog/Post.md`.
 * @property position Where in [file] the report is about, if known.
 */
internal class MarkdownLocation(
    val file: File,
    val relativePath: Path,
    val position: Position? = null,
) {
    /** A line and column in a file, both starting at 1. */
    class Position(val line: Int, val column: Int)
}

/** Formats this location like Kotlin's own errors do, e.g. `file:///.../blog/Post.md:12:9`, which IDEs turn into links. */
internal fun MarkdownLocation.toAbsoluteString() = file.toPath().toUri().toString() + positionSuffix()

/** Formats this location relative to its Markdown root, e.g. `blog/Post.md:12:9`. */
internal fun MarkdownLocation.toRelativeString() = relativePath.invariantSeparatorsPathString + positionSuffix()

private fun MarkdownLocation.positionSuffix() = position?.let { ":${it.line}:${it.column}" }.orEmpty()
