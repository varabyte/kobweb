package com.varabyte.kobweb.silk.components.navigation

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.navigation.Anchor
import com.varabyte.kobweb.navigation.OpenLinkStrategy
import com.varabyte.kobweb.navigation.UpdateHistoryMode
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.addVariant
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.selectors.link
import com.varabyte.kobweb.silk.style.selectors.visited
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.style.vars.color.ColorVar
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

object LinkVars {
    val DefaultColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val VisitedColor by StyleVariable<CSSColorValue>(prefix = "silk")
}

sealed interface LinkKind : ComponentKind

/**
 * Style to use with [A] tags to give them Silk-themed colors.
 */
val LinkStyle = CssStyle<LinkKind> {
    base {
        Modifier.textDecorationLine(TextDecorationLine.None)
    }

    link {
        Modifier.color(LinkVars.DefaultColor.value())
    }
    visited {
        Modifier.color(LinkVars.VisitedColor.value())
    }
    hover {
        Modifier.textDecorationLine(TextDecorationLine.Underline)
    }
}

val UndecoratedLinkVariant = LinkStyle.addVariant {
    hover {
        Modifier.textDecorationLine(TextDecorationLine.None)
    }
}

val UncoloredLinkVariant = LinkStyle.addVariant {
    val colorModifier = Modifier.color(ColorVar.value())
    link { colorModifier }
    visited { colorModifier }
}

val AlwaysUnderlinedLinkVariant = LinkStyle.addVariant {
    base {
        Modifier.textDecorationLine(TextDecorationLine.Underline)
    }
}


/**
 * Linkable text which, when clicked, navigates to the target [path].
 *
 * This composable is SilkTheme-aware, and if colors are not specified, will automatically use the current theme plus
 * color mode.
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
fun Link(
    path: String,
    text: String? = null,
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<LinkKind>? = null,
    openInternalLinksStrategy: OpenLinkStrategy? = null,
    openExternalLinksStrategy: OpenLinkStrategy? = null,
    updateHistoryMode: UpdateHistoryMode? = null,
    ref: ElementRefScope<HTMLElement>? = null,
) {
    Link(
        path,
        modifier,
        variant,
        openInternalLinksStrategy,
        openExternalLinksStrategy,
        updateHistoryMode,
        ref
    ) {
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
    variant: CssStyleVariant<LinkKind>? = null,
    openInternalLinksStrategy: OpenLinkStrategy? = null,
    openExternalLinksStrategy: OpenLinkStrategy? = null,
    updateHistoryMode: UpdateHistoryMode? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable () -> Unit
) {
    Anchor(
        href = path,
        attrs = LinkStyle.toModifier(variant).then(modifier).toAttrs(),
        openInternalLinksStrategy,
        openExternalLinksStrategy,
        updateHistoryMode,
    ) {
        registerRefScope(ref)
        content()
    }
}
