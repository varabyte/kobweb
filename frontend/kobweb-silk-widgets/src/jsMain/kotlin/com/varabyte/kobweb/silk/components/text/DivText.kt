package com.varabyte.kobweb.silk.components.text

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

// Normally, the color of the text is inherited from its parent (see SurfaceStyle), but users may still want to
// override text styles globally in their own app, so we still register a style here even if it's empty
val DivTextStyle by ComponentStyle(prefix = "silk") {}

/**
 * A text wrapped in a div block, which can be styled based on a passed-in `Modifier`.
 *
 * Essentially a convenient shortcut for
 *
 * ```
 * Div(attrs = modifier.toAttrs()) {
 *   Text("Some text")
 * }
 * ```
 *
 * This class is useful for declaring that some text is generally meant to stand alone, separate from the rest of your
 * document.
 *
 * See also: [SpanText]. If you're not sure which to use between DivTest and SpanText, SpanText is probably safer, as
 * spans are meant for styles that apply to a range (which is natural for text), while divs are meant for styles that
 * apply to a whole, standalone block. As such, DivText may affect your document flow in unexpected ways. If this
 * composable is called in the context of a parent flexbox composable (such as Row or Column), they will both be treated
 * the same way, so in those cases which you choose may not matter functionally but can be used to express intention.
 */
@Composable
fun DivText(
    text: String,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    ref: ElementRefScope<HTMLDivElement>? = null,
) {
    val finalModifier = DivTextStyle
        .toModifier(variant)
        .then(modifier)

    Div(attrs = finalModifier.toAttrs()) {
        registerRefScope(ref)
        Text(text)
    }
}
