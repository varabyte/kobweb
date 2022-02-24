package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.style.toClassName
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.attrsModifier
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.HTMLElement

class RowScope {
    fun Modifier.align(alignment: Alignment.Vertical) = attrsModifier {
        classes("${alignment.toClassName()}-self")
    }
}

@Composable
fun Row(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    elementScope: (@Composable ElementScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    Div(modifier.asAttributesBuilder {
        classes("kobweb-row", horizontalArrangement.toClassName(), verticalAlignment.toClassName())
    }) {
        elementScope?.invoke(this)
        RowScope().content()
    }
}