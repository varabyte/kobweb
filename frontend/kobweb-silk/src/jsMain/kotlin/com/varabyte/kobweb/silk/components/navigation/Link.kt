package com.varabyte.kobweb.silk.components.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.dom.Text
import com.varabyte.kobweb.navigation.Link as KobwebLink

/**
 * Linkable text which, when clicked, navigates to the target [path].
 *
 * This composable is SilkTheme-aware, and if colors are not specified, will automatically use the current theme plus
 * color mode.
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
    autoPrefix: Boolean = true,
) {
    Link(path, modifier, variant, autoPrefix) {
        Text(text ?: path)
    }
}

/**
 * Linkable content which, when clicked, navigates to the target [path].
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You usually
 *   want this to be true, unless you are intentionally linking outside this site's root folder while still staying in
 *   the same domain.
 */
@Composable
fun Link(
    path: String,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    autoPrefix: Boolean = true,
    content: @Composable () -> Unit = {}
) {
    KobwebLink(
        href = path,
        attrs = LinkStyle.toModifier(variant).then(modifier).asAttributesBuilder(),
        autoPrefix
    ) {
        content()
    }
}
