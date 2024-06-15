package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.style.vars.color.BorderColorVar
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Hr

object DividerVars {
    val Color by StyleVariable(prefix = "silk", defaultFallback = BorderColorVar.value())
    val Length by StyleVariable<CSSLengthOrPercentageNumericValue>(prefix = "silk", defaultFallback = 90.percent)
}

sealed interface HorizontalDividerKind : ComponentKind

val HorizontalDividerStyle = CssStyle.base<HorizontalDividerKind> {
    Modifier
        .borderTop(1.px, LineStyle.Solid, DividerVars.Color.value())
        .width(DividerVars.Length.value())
}

/**
 * A dividing line (i.e. an `<hr>` tag) which is SilkTheme-aware meant to visually break up elements in a column.
 */
@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<HorizontalDividerKind>? = null,
) {
    Hr(HorizontalDividerStyle.toModifier(variant).then(modifier).toAttrs())
}

sealed interface VerticalDividerKind : ComponentKind

val VerticalDividerStyle = CssStyle.base<VerticalDividerKind> {
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
    variant: CssStyleVariant<VerticalDividerKind>? = null,
) {
    Hr(VerticalDividerStyle.toModifier(variant).then(modifier).toAttrs())
}
