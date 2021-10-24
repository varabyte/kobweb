package com.varabyte.kobweb.silk.components.text

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.color
import com.varabyte.kobweb.silk.components.ComponentKey
import com.varabyte.kobweb.silk.components.ComponentStyle
import com.varabyte.kobweb.silk.components.ComponentVariant
import com.varabyte.kobweb.silk.components.EmptyState
import com.varabyte.kobweb.silk.components.toModifier
import com.varabyte.kobweb.silk.theme.SilkTheme
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

interface TextStyle : ComponentStyle<EmptyState>
class DefaultTextStyle : TextStyle {
    @Composable
    @ReadOnlyComposable
    override fun toModifier(state: EmptyState): Modifier {
        return Modifier.color(SilkTheme.palette.onPrimary)
    }
}

object TextKey : ComponentKey<TextStyle>
interface TextVariant : ComponentVariant<EmptyState, TextStyle>

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
    variant: TextVariant? = null
) {
    Span(
        attrs = SilkTheme.componentStyles[TextKey].toModifier(variant)
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