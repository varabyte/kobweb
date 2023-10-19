package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.addVariantBase
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.style.vars.color.BorderColorVar
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Hr

object DividerVars {
    val Color by StyleVariable(prefix = "silk", defaultFallback = BorderColorVar.value())
}

val DividerStyle by ComponentStyle.base(prefix = "silk") {
    Modifier
        .borderTop(1.px, LineStyle.Solid, DividerVars.Color.value())
        .fillMaxWidth(90.percent)
}

val VerticalDividerVariant by DividerStyle.addVariantBase {
    Modifier
        .borderTop { width(0.px) }
        .maxWidth(0.percent)
        .borderLeft(1.px, LineStyle.Solid, DividerVars.Color.value())
        .fillMaxHeight(90.percent)
}

/**
 * A dividing line (i.e. an `<hr>` tag) which is SilkTheme-aware.
 */
@Composable
fun Divider(
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
) {
    Hr(DividerStyle.toModifier(variant).then(modifier).toAttrs())
}
