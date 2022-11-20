package com.varabyte.kobweb.silk.components.text

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

// Normally, the color of the text is inherited from its parent (see SurfaceStyle), but users may still want to
// override text styles globally in their own app, so we still register a style here even if it's empty
val SpanTextStyle = ComponentStyle("silk-span-text") { }

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
 * Note it is perfectly fine (and expected) for callers to reach to the `Text` method that comes from *Compose for Web*
 * in most cases.
 *
 * This composable is SilkTheme-aware, and if colors are not specified, will automatically use the current theme plus
 * color mode.
 */
@Composable
fun SpanText(
    text: String,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null
) {
    val finalModifier = SpanTextStyle
        .toModifier(variant)
        .then(modifier)
        .thenIf(text.startsWith(' ') || text.endsWith(' ')) {
            Modifier.whiteSpace(WhiteSpace.PreWrap)
        }

    Span(attrs = finalModifier.toAttrs()) {
        Text(text)
    }
}

// TODO(#168): Remove in v1.0
@Deprecated("'TextStyle' was renamed to 'SpanTextStyle'.", ReplaceWith("SpanTextStyle"))
val TextStyle = SpanTextStyle

// TODO(#168): Remove in v1.0
@Deprecated("'Text' was renamed to 'SpanText'.", ReplaceWith("SpanText(text, modifier, variant)"))
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null
) {
    SpanText(text, modifier, variant)
}
