package com.varabyte.kobweb.silk.components.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderColor
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Hr

val DividerStyle = ComponentStyle.base("silk-divider") {
    Modifier
        .borderTop(1.px, LineStyle.Solid, colorMode.toSilkPalette().border.toCssColor())
        .fillMaxWidth(90.percent)
}

/**
 * A dividing line (i.e. an `<hr>` tag) which is SilkTheme-aware.
 */
@Composable
fun Divider(modifier: Modifier = Modifier, variant: ComponentVariant? = null) {
    Hr(DividerStyle.toModifier(variant).then(modifier).asAttributesBuilder())
}