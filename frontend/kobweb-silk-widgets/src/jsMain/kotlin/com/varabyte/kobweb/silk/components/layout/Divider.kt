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
import com.varabyte.kobweb.silk.theme.colors.BorderColorVar
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Hr

val DividerColorVar by StyleVariable(prefix = "silk", defaultFallback = BorderColorVar.value())

val DividerStyle by ComponentStyle.base(prefix = "silk") {
    Modifier
        .borderTop(1.px, LineStyle.Solid, DividerColorVar.value())
        .fillMaxWidth(90.percent)
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
