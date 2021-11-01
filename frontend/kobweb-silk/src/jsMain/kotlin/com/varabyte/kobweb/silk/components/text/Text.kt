package com.varabyte.kobweb.silk.components.text

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.color
import com.varabyte.kobweb.silk.components.ComponentKey
import com.varabyte.kobweb.silk.components.ComponentModifier
import com.varabyte.kobweb.silk.components.then
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

val TextKey = ComponentKey("silk-text")
object DefaultTextModifier : ComponentModifier {
    @Composable
    override fun toModifier(data: Any?): Modifier {
        return Modifier.color(SilkTheme.palette.onPrimary)
    }
}

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
    variant: ComponentModifier? = null
) {
    Span(
        attrs = SilkTheme.componentModifiers[TextKey].then(variant).toModifier(null)
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