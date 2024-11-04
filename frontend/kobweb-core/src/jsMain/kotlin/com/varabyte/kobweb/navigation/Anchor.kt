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
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front of [href] if
 *   possible. For example, if the [href] parameter was set to "/about" and the site's route prefix was set to
 *   "company/our-team", then the `href` value will be converted to "/company/our-team/about". You usually want this to
 *   be true, unless you are intentionally linking outside this site's root folder while still staying in the same
 *   domain, e.g. you are linking to "/company/other-team". See [RoutePrefix] for more information.
 */
@Composable
fun Anchor(
    href: String,
    attrs: AttrBuilderContext<HTMLAnchorElement>? = null,
    openInternalLinksStrategy: OpenLinkStrategy? = null,
    openExternalLinksStrategy: OpenLinkStrategy? = null,
    updateHistoryMode: UpdateHistoryMode? = null,
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
                    // No auto-prefix, as we handled it ourselves (see href modification above). We did this so that the
                    // URL associated with the anchor element (that additionally shows up at the bottom of the browser
                    // window) matches where we actually navigate to.
                    autoPrefix = false
                )
                evt.preventDefault()
                evt.stopPropagation()
            }
        },
        content
    )
}
