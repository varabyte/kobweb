package com.varabyte.kobweb.silk.components.text

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

// Normally, the color of the text is inherited from its parent (see SurfaceStyle), but users may still want to
// override text styles globally in their own app, so we still register a style here even if it's empty
val TextStyle = ComponentStyle("silk-text") { }

/**
 * A span of text.
 *
 * This composable is SilkTheme-aware, and if colors are not specified, will automatically use the current theme plus
 * color mode.
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null
) {
    val spaceModifier = Modifier.whiteSpace(WhiteSpace.PreWrap).takeIf { text.startsWith(' ') || text.endsWith(' ') } ?: Modifier
    val finalModifier = TextStyle.toModifier(variant).then(modifier).then(spaceModifier)

    if (finalModifier !== Modifier) {
        Span(attrs = finalModifier.asAttributesBuilder()) {
            Text(text)
        }
    } else {
        Text(text)
    }
}