package com.varabyte.kobweb.silk.components.text

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLSpanElement

interface SpanTextKind : ComponentKind

// NOTE: This component lives in `silk-foundation` and not `silk-widgets` because it's not really a widget, but rather
// a fairly general, useful, opinionated convenience pattern that people might want to use even if they don't want to
// bring in Silk UI / are working with a different UI framework.

// Normally, the color of the text is inherited from its parent (see SurfaceStyle), but users may still want to
// override text styles globally in their own app, so we still register a style here even if it's empty
val SpanTextStyle = CssStyle<SpanTextKind> {}

/**
 * A span of text, which can be styled based on a passed-in `Modifier`.
 *
 * Essentially a convenient shortcut for
 *
 * ```
 * Span(attrs = modifier.toAttrs()) {
 *   Text("Some text")
 * }
 * ```
 *
 * This class is useful for creating an umbrella span on top of a bunch of text that you additionally want to apply
 * some `Modifier` styles to. It may also be useful if you want to ensure that a leading or trailing space doesn't get
 * swallowed, which `Text` suffers from in some cases (e.g. within a parent container that uses a flex display).
 *
 * Note it is perfectly fine (and expected) for callers to reach to the `Text` method that comes from *Compose HTML*
 * in most cases.
 */
@Composable
fun SpanText(
    text: String,
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<SpanTextKind>? = null,
    ref: ElementRefScope<HTMLSpanElement>? = null,
) {
    val finalModifier = SpanTextStyle
        .toModifier(variant)
        .then(modifier)
        .thenIf(text.startsWith(' ') || text.endsWith(' ')) {
            Modifier.whiteSpace(WhiteSpace.PreWrap)
        }

    Span(attrs = finalModifier.toAttrs()) {
        registerRefScope(ref)
        Text(text)
    }
}
