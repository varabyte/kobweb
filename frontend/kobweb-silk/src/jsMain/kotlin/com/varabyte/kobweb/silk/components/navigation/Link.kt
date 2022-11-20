package com.varabyte.kobweb.silk.components.navigation

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.navigation.Anchor
import com.varabyte.kobweb.navigation.OpenLinkStrategy
import com.varabyte.kobweb.navigation.toOpenLinkStrategy
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

/**
 * Linkable text which, when clicked, navigates to the target [path].
 *
 * This composable is SilkTheme-aware, and if colors are not specified, will automatically use the current theme plus
 * color mode.
 *
 * @param openInternalLinksStrategy If set, force the behavior of how internal links (links under the site's root) open.
 *   If not set, this behavior will be determined depending on what control keys are being pressed. See
 *   [SyntheticMouseEvent.toOpenLinkStrategy] to see which key modifier combinations result in which behavior.
 *
 * @param openExternalLinksStrategy If set, force the behavior of how external links open (links outside this site's
 *   domain). If not set, this behavior will be determined depending on what control keys are being pressed. See
 *   [SyntheticMouseEvent.toOpenLinkStrategy] to see which key modifier combinations result in which behavior.
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You usually
 *   want this to be true, unless you are intentionally linking outside this site's root folder while still staying in
 *   the same domain.
 */
@Composable
fun Link(
    path: String,
    text: String? = null,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    openInternalLinksStrategy: OpenLinkStrategy? = null,
    openExternalLinksStrategy: OpenLinkStrategy? = null,
    autoPrefix: Boolean = true,
    ref: ElementRefScope<HTMLElement>? = null,
) {
    Link(path, modifier, variant, openInternalLinksStrategy, openExternalLinksStrategy, autoPrefix, ref) {
        Text(text ?: path)
    }
}

/**
 * Linkable content which, when clicked, navigates to the target [path].
 *
 * See the other [Link] docs for parameter details.
 */
@Composable
fun Link(
    path: String,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    openInternalLinksStrategy: OpenLinkStrategy? = null,
    openExternalLinksStrategy: OpenLinkStrategy? = null,
    autoPrefix: Boolean = true,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable () -> Unit = {}
) {
    Anchor(
        href = path,
        attrs = LinkStyle.toModifier(variant).then(modifier).toAttrs(),
        openInternalLinksStrategy,
        openExternalLinksStrategy,
        autoPrefix
    ) {
        registerRefScope(ref)
        content()
    }
}
