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
 *   // Markdown front matter value can be a list of strings, but here it's only a single one
 *   val author = ctx.markdown.frontMatter.getValue("author").single()
 *   Text("Article by $author")
 * }
 * ```
 *
 * @param frontMatter Exposes data set in a markdown file's front matter block.
 *   See also: https://github.com/varabyte/kobweb#front-matter
 */
class MarkdownContext(
    val frontMatter: Map<String, List<String>>
)

// Extend `rememberPageContext()` with markdown specific values
val PageContext.markdown: MarkdownContext
    @Composable
    @ReadOnlyComposable
    get() = LocalMarkdownContext.current

val LocalMarkdownContext = compositionLocalOf { MarkdownContext(emptyMap()) }