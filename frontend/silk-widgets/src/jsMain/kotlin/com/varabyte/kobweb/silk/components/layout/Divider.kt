package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.style.vars.color.BorderColorVar
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Hr

object DividerVars {
    val Color by StyleVariable(prefix = "silk", defaultFallback = BorderColorVar.value())
    val Length: StyleVariable.PropertyValue<CSSLengthOrPercentageValue> by StyleVariable(prefix = "silk", defaultFallback = 90.percent)
}

/**
 * A dividing line (i.e. an `<hr>` tag) which is SilkTheme-aware.
 */
@Deprecated("Divider was renamed to HorizontalDivider.", ReplaceWith("HorizontalDivider(modifier, variant)"))
@Composable
fun Divider(
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
) {
    Hr(HorizontalDividerStyle.toModifier(variant).then(modifier).toAttrs())
}

val HorizontalDividerStyle by ComponentStyle.base(prefix = "silk") {
    Modifier
        .borderTop(1.px, LineStyle.Solid, DividerVars.Color.value())
        .width(DividerVars.Length.value())
}

@Deprecated(
    "Divider was renamed to HorizontalDivider. Use HorizontalDividerStyle instead.",
    ReplaceWith("HorizontalDividerStyle")
)
val DividerStyle = HorizontalDividerStyle

/**
 * A dividing line (i.e. an `<hr>` tag) which is SilkTheme-aware meant to visually break up elements in a column.
 */
@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
) {
    Hr(HorizontalDividerStyle.toModifier(variant).then(modifier).toAttrs())
}

val VerticalDividerStyle by ComponentStyle.base(prefix = "silk") {
    Modifier
        .borderLeft(1.px, LineStyle.Solid, DividerVars.Color.value())
        .height(DividerVars.Length.value())
}

/**
 * A dividing line (i.e. an `<hr>` tag) which is SilkTheme-aware meant to visually break up elements in a row.
 */
@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
) {
    Hr(VerticalDividerStyle.toModifier(variant).then(modifier).toAttrs())
}
