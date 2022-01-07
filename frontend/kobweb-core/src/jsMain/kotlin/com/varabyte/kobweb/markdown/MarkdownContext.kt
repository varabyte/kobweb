package com.varabyte.kobweb.markdown

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.PageContext

class MarkdownContext(
    val frontMatter: Map<String, Any>
)

val PageContext.markdown: MarkdownContext
    @Composable
    @ReadOnlyComposable
    get() = LocalMarkdownContext.current

val LocalMarkdownContext = compositionLocalOf { MarkdownContext(emptyMap()) }