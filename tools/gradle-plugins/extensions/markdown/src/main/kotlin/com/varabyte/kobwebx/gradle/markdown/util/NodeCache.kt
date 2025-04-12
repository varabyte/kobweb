package com.varabyte.kobwebx.gradle.markdown.util

import com.varabyte.kobwebx.gradle.markdown.KotlinRenderer
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import java.io.File
import java.io.IOException
import java.nio.file.Path

/**
 * Class which maintains a cache of parsed markdown content associated with their source files.
 *
 * This cache is useful because Markdown files can reference other Markdown files, meaning as we process a
 * collection of them, we might end up referencing the same file multiple times.
 *
 * Note that this cache should not be created with too long a lifetime, because users may edit Markdown files and
 * those changes should be picked up. It is intended to be used only for a single processing run across a collection
 * of markdown files and then discarded.
 *
 * @param parser The parser to use to parse markdown files.
 * @param roots A collection of root folders under which Markdown files should be considered for processing. Any
 *   markdown files referenced outside of these roots should be ignored for caching purposes.
 */
internal class NodeCache(private val parser: Parser, private val roots: Set<File>) {
    /**
     * Additional metadata to associate with the [Node] returned by [NodeCache.get].
     *
     * This metadata allows us to avoid passing in a bunch of random parameters to the [KotlinRenderer] and also
     * allows us to potentially share metadata across multiple nodes.
     */
    @JvmInline
    internal value class Metadata(private val map: MutableMap<Node, Entry> = mutableMapOf()) {
        class Entry(
            val projectRoot: String,
            val markdownSourceRoot: Path,
            val sourceFilePath: Path,
            val outputRootPath: Path,
            val `package`: String,
            val routeWithSlug: String?,
        ) {
            val routeWithoutSlug = routeWithSlug?.let { it.substringBeforeLast("/") + "/" }
        }

        operator fun set(node: Node, entry: Entry) {
            map[node] = entry
        }

        operator fun get(node: Node): Entry? = map[node]

        fun getValue(node: Node): Entry = map.getValue(node)
    }

    val metadata = Metadata()
    private val existingNodes = mutableMapOf<String, Node>()

    /**
     * Returns a parsed Markdown [Node] for the target file (which is expected to be a valid markdown file).
     *
     * Once queried, the node will be cached so that subsequent calls to this method will not re-read the file. If
     * the file fails to parse, this method will throw an exception.
     */
    operator fun get(file: File): Node = file.canonicalFile.let { canonicalFile ->
        require(roots.any { canonicalFile.startsWith(it) }) {
            "File $canonicalFile is not under any of the specified Markdown roots: $roots"
        }
        existingNodes.computeIfAbsent(canonicalFile.invariantSeparatorsPath) {
            parser.parse(canonicalFile.readText())
        }
    }

    /**
     * Returns a parsed Markdown node given a relative path which will be resolved against all markdown roots.
     *
     * For example, "test/example.md" will return parsed markdown information if found in
     * `src/jsMain/resources/markdown/test/example.md`.
     *
     * This will return null if:
     * * no file is found matching the passed in path.
     * * the file at the specified location fails to parse.
     * * the relative file path escapes the current root, e.g. `../public/files/license.md`, as this could be a
     *   useful way to link to a raw markdown file that should be served as is and not converted into an html page.
     */
    fun getRelative(relPath: String): Node? = try {
        roots.asSequence()
            .map { it to it.resolve(relPath).canonicalFile }
            // Make sure we don't access anything outside our markdown roots
            .firstOrNull { (root, canonicalFile) ->
                canonicalFile.exists() && canonicalFile.isFile && canonicalFile.startsWith(root)
            }?.second?.let(::get)
    } catch (_: IOException) {
        null
    }
}
