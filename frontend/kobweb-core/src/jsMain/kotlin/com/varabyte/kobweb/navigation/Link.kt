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
 *
 * @param openInternalLinksStrategy If set, force the behavior of how internal links (links under the site's root) open.
 *   If not set, this behavior will be determined depending on what control keys are being pressed.
 *
 * @param openExternalLinksStrategy If set, force the behavior of how external links open (links outside this site's
 *   domain). If not set, this behavior will be determined depending on what control keys are being pressed.
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You usually
 *   want this to be true, unless you are intentionally linking outside this site's root folder while still staying in
 *   the same domain.
 */
@Composable
fun Link(
    href: String,
    attrs: AttrBuilderContext<HTMLAnchorElement>? = null,
    openInternalLinksStrategy: OpenLinkStrategy? = null,
    openExternalLinksStrategy: OpenLinkStrategy? = null,
    autoPrefix: Boolean = true,
    content: ContentBuilder<HTMLAnchorElement>? = null
) {
    @Suppress("NAME_SHADOWING") // Intentional shadowing for in-place transformation
    val href = RoutePrefix.prependIf(autoPrefix, href)

    val ctx = rememberPageContext()
    A(
        href,
        attrs = {
            if (attrs != null) {
                attrs()
            }
            onClick { evt ->
                @Suppress("NAME_SHADOWING") // Intentional shadowing - nullable to non-null
                val openInternalLinksStrategy = openInternalLinksStrategy ?: evt.toOpenLinkStrategy()
                if (openExternalLinksStrategy == null) {
                    if (ctx.router.routeTo(href, openLinkStrategy = openInternalLinksStrategy)) {
                        evt.preventDefault()
                    }
                }
                else {
                    ctx.router.navigateTo(href, openInternalLinksStrategy = openInternalLinksStrategy, openExternalLinksStrategy = openExternalLinksStrategy)
                    evt.preventDefault()
                }
            }
        },
        content
    )
}