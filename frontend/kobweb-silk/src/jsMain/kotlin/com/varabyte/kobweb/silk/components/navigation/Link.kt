package com.varabyte.kobweb.silk.components.navigation

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.navigation.OpenLinkStrategy
import com.varabyte.kobweb.navigation.toOpenLinkStrategy
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLAnchorElement
import com.varabyte.kobweb.navigation.Link as KobwebLink

/**
 * Linkable text which, when clicked, navigates to the target [path].
 *
 * This composable is SilkTheme-aware, and if colors are not specified, will automatically use the current theme plus
 * color mode.
 *
 * @param openInternalLinksStrategy If set, force the behavior of how internal links (links under the site's root) open.
 *   If not set, this behavior will be determined depending on what control keys are being pressed. See
 *   [SyntheticMouseEvent.toOpenLinkStrategy] for more information.
 *
 * @param openExternalLinksStrategy If set, force the behavior of how external links open (links outside this site's
 *   domain). If not set, this behavior will be determined depending on what control keys are being pressed. See
 *   [SyntheticMouseEvent.toOpenLinkStrategy] for more information.
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
    elementScope: (@Composable ElementScope<HTMLAnchorElement>.() -> Unit)? = null,
) {
    Link(path, modifier, variant, openInternalLinksStrategy, openExternalLinksStrategy, autoPrefix, elementScope) {
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
    elementScope: (@Composable ElementScope<HTMLAnchorElement>.() -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    KobwebLink(
        href = path,
        attrs = LinkStyle.toModifier(variant).then(modifier).asAttributesBuilder(),
        openInternalLinksStrategy,
        openExternalLinksStrategy,
        autoPrefix
    ) {
        elementScope?.invoke(this)
        content()
    }
}
