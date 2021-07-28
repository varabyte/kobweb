package nekt.core.components

import androidx.compose.runtime.Composable
import nekt.core.Router
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.w3c.dom.HTMLAnchorElement

/**
 * A tag which expects a path to another page in this site.
 *
 * Semantically, this component is styled like an `<a>` tag.
 *
 * @param path A relative "href" value that represents a path within this site, e.g. "/about", "/posts/all", etc.
 */
@Composable
fun Link(
    path: String,
    attrs: AttrBuilderContext<HTMLAnchorElement>? = null,
    content: ContentBuilder<HTMLAnchorElement>? = null,
) {
    A(
        null,
        attrs = {
            if (attrs != null) {
                attrs()
            }
            onClick { e ->
                e.preventDefault()
                Router.navigateTo(path)
            }
        },
        content
    )
}