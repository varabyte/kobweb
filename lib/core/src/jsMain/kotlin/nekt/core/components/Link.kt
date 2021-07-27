package nekt.core.components

import androidx.compose.runtime.Composable
import nekt.core.Router
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLAnchorElement

@Composable
fun Link(
    href: String? = null,
    attrs: AttrBuilderContext<HTMLAnchorElement>? = null,
    content: ContentBuilder<HTMLAnchorElement>? = null,
) {
    A(
        href,
        {
            if (attrs != null) {
                attrs()
            }
            if (href != null) {
                onClick { e ->
                    e.preventDefault()
                    Router.navigateTo(href)
                }
            }
        },
        content
    )
}