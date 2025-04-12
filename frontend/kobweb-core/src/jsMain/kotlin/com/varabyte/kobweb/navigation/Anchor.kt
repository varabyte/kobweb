package com.varabyte.kobweb.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.init.KobwebConfig
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
 *   If not set, this behavior will default to in place. Note that this behavior may be overridden by the browser based
 *   on keyboard/mouse shortcuts.
 *
 * @param openExternalLinksStrategy If set, force the behavior of how external links open (links outside this site's
 *   domain). If not set, this behavior will default to in a new tab. Note that this behavior may be overridden by the
 *   browser based on keyboard/mouse shortcuts.
 *
 * @param updateHistoryMode If set, follow this behavior when following the link. By default, history will be added
 *   when visiting the new location (so you can return back to the current page), but [UpdateHistoryMode.REPLACE] can be
 *   used to create an effect where the new page "takes over" the current page in place.
 */
@Composable
fun Anchor(
    href: String,
    attrs: AttrBuilderContext<HTMLAnchorElement>? = null,
    openInternalLinksStrategy: OpenLinkStrategy? = null,
    openExternalLinksStrategy: OpenLinkStrategy? = null,
    updateHistoryMode: UpdateHistoryMode? = null,
    content: ContentBuilder<HTMLAnchorElement>? = null
) {
    val ctx = rememberPageContext()
    A(
        BasePath.prependTo(href), // match `navigateTo` which internally prepends as well
        attrs = {
            if (attrs != null) {
                attrs()
            }
            @Suppress("NAME_SHADOWING") // Intentional shadowing - nullable to non-null
            onClick { evt ->
                val openInternalLinksStrategy = openInternalLinksStrategy
                    ?: evt.toOpenLinkStrategy(KobwebConfig.Instance.openLinkStrategies.internal)
                val openExternalLinksStrategy = openExternalLinksStrategy
                    ?: evt.toOpenLinkStrategy(KobwebConfig.Instance.openLinkStrategies.external)
                ctx.router.navigateTo(
                    href,
                    updateHistoryMode ?: UpdateHistoryMode.PUSH,
                    openInternalLinksStrategy = openInternalLinksStrategy,
                    openExternalLinksStrategy = openExternalLinksStrategy,
                )
                evt.preventDefault()
                evt.stopPropagation()
            }
        },
        content
    )
}
