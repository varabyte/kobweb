package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.rememberPageContext
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.w3c.dom.HTMLAnchorElement

/**
 * A special version of the A tag which, when clicked on, will not reload the current page if it's within the
 * same site.
 *
 * Instead, it will use the Kobweb [Router] to automatically re-render the content of the page without needing to hit
 * a server.
 */
@Composable
fun Link(
    href: String,
    attrs: AttrBuilderContext<HTMLAnchorElement>? = null,
    content: ContentBuilder<HTMLAnchorElement>? = null) {
    val ctx = rememberPageContext()
    A(
        href,
        attrs = {
            if (attrs != null) {
                attrs()
            }
            onClick { evt ->
                evt.preventDefault()
                ctx.router.navigateTo(href)
            }
        },
        content
    )
}