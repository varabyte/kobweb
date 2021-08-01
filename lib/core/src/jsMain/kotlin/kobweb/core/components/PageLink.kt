package kobweb.core.components

import androidx.compose.runtime.Composable
import kobweb.core.Router
import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.w3c.dom.Element
import org.w3c.dom.HTMLAnchorElement

/**
 * @param path A relative "href" value that represents a path within this site, e.g. "/about", "/posts/all", etc.
 */
data class PageLinkParams(
    val path: String,
)

/**
 * Call on a target attrs builder to set this component as a page link.
 *
 * Once done, the element will intercept click handling and route the user to a different page on the site.
 *
 * For example, you could write:
 *
 * ```
 * A({
 *   attrs = {
 *     makePageLink("/about")
 *   }
 * }) {
 *   ...
 * }
 * ```
 *
 * As this is relatively common to do, a helper [PageLink] method is provided as well which does this for you.
 */
fun <E : Element> AttrsBuilder<E>.setAsPageLink(params: PageLinkParams) {
    onClick { e ->
        e.preventDefault()
        Router.navigateTo(params.path)
    }
}

/**
 * A tag which expects a path to another page in this site.
 *
 * Semantically, this component is styled like an `<a>` tag.
 */
@Composable
fun PageLink(
    params: PageLinkParams,
    attrs: AttrBuilderContext<HTMLAnchorElement>? = null,
    content: ContentBuilder<HTMLAnchorElement>? = null,
) {
    A(
        null,
        attrs = {
            if (attrs != null) {
                attrs()
            }
            setAsPageLink(params)
        },
        content
    )
}