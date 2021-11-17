package com.varabyte.kobweb.silk.components.text

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.whiteSpace
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
    Span(
        attrs = TextStyle.toModifier(variant)
            .then(modifier)
            .asAttributeBuilder {
                if (text.startsWith(' ') || text.endsWith(' ')) {
                    style {
                        // Prevent spaces in text from being collapsed
                        whiteSpace("pre-wrap")
                    }
                }
            }
    ) {
        Text(text)
    }
}