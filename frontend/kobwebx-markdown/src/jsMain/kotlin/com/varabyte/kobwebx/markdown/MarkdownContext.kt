package com.varabyte.kobwebx.markdown

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.PageContext

/**
 * Various context that will be set if this page was generated from Markdown.
 *
 * To access it, use `rememberPageContext` in your composable and read the `markdown` field.
 *
 * In other words, if you had Markdown like this, which called a `Signature` widget:
 *
 * ```markdown
 * # Markdown.md
 *
 * ---
 * author: BitSpittle
 * date: 12-31-1999
 * ---
 *
 * ... a bunch of content ...
 *
 * {{ Signature() }}
 *```
 *
 * then you might implement `Signature` like so:
 *
 * ```
 * // Signature.kt
 *
 * @Composable
 * fun Signature() {
 *   val ctx = rememberPageContext()
 *   val markdown = ctx.markdown!! // Will be null if this composable was not called from within a markdown file
 *   // Markdown front matter value can be a list of strings, but here it's only a single one
 *   val author = markdown.frontMatter.getValue("author").single()
 *   Text("Article by $author")
 * }
 * ```
 *
 * @param markdownRoot Which source markdown directory contains the markdown used to generate this page, relative to
 *   the project it lives in, e.g. `src/jsMain/resources/markdown`.
 * @param path The path of the markdown file, including its extension, relative to [markdownRoot],
 *   e.g. `a/b/c/Hello.md`.
 * @param frontMatter Exposes data set in a markdown file's front matter block. (Front matter is the YAML block
 *   optionally declared at the top of a markdown file, which is delimited by `---` lines.)
 */
class MarkdownContext(
    val projectRoot: String,
    val markdownRoot: String,
    val path: String,
    val frontMatter: Map<String, List<String>>,
) {
    /**
     * The full path of the markdown file, including its extension, relative to its owning codebase.
     *
     * For example, `site/src/jsMain/resources/markdown/a/b/c/Hello.md`.
     *
     * This could be a convenient property to use if you wanted to add a link to, say, your GitHub repository for the
     * source of this page.
     */
    val fullPath: String get() = listOf(projectRoot, markdownRoot, path).joinToString("/")
}

// Extend `rememberPageContext()` with markdown specific values
@Suppress("UnusedReceiverParameter") // Useful for scoping access to the feature only to pages
val PageContext.markdown: MarkdownContext?
    @Composable
    @ReadOnlyComposable
    get() = LocalMarkdownContext.current

val LocalMarkdownContext = compositionLocalOf<MarkdownContext?> { null }